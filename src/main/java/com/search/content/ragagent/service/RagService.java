package com.search.content.ragagent.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public RagService(VectorStore vectorStore, ChatClient.Builder chatClientBuilder) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClientBuilder.build();
    }

    public int indexPdf(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("El archivo no existe en la ruta especificada: " + filePath);
        }

        // 1. Leer el documento PDF
        var pdfReader = new PagePdfDocumentReader(
                new FileSystemResource(file),
                PdfDocumentReaderConfig.builder().build()
        );

        List<Document> documents = pdfReader.get();

        // 2. Dividir el texto en fragmentos (chunks)
        var textSplitter = new TokenTextSplitter(800, 350, 5, 10000, true);
        List<Document> chunks = textSplitter.apply(documents);

        // 3. Guardar en MongoDB Atlas Vector Store
        vectorStore.accept(chunks);

        return chunks.size();
    }

    public String askQuestion(String query) {
        // 1. Buscar fragmentos similares en MongoDB Atlas
        List<Document> similarDocs = vectorStore.similaritySearch(
                SearchRequest.query(query).withTopK(4)
        );

        String context = similarDocs.stream()
                .map(Document::getContent)
                .collect(Collectors.joining("\n---\n"));

        // 2. Construir el prompt con contexto RAG
        String prompt = """
                Eres un asistente académico experto en Ingeniería de Software, Patrones de Diseño, LLMs y Prompts.
                Responde a la pregunta del usuario utilizando ÚNICAMENTE la siguiente información de contexto recuperada de los artículos indexados.
                Si la respuesta no se encuentra en el contexto, indica amablemente que no tienes suficiente información en los documentos cargados.

                CONTEXTO RECUPERADO:
                %s

                PREGUNTA DEL USUARIO:
                %s
                """.formatted(context, query);

        return chatClient.prompt(new Prompt(prompt)).call().content();
    }
}
