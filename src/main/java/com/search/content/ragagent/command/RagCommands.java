package com.search.content.ragagent.command;

import com.search.content.ragagent.service.RagService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class RagCommands {

    private final RagService ragService;

    public RagCommands(RagService ragService) {
        this.ragService = ragService;
    }

    @ShellMethod(key = "cargar-pdf", value = "Indexa un archivo PDF en la base de datos vectorial")
    public String loadPdf(@ShellOption(help = "Ruta absoluta o relativa del archivo PDF") String rutaFile) {
        try {
            int chunks = ragService.indexPdf(rutaFile);
            return "PDF indexado correctamente. Se generaron y guardaron " + chunks + " fragmentos vectoriales.";
        } catch (Exception e) {
            return "Error al procesar el PDF: " + e.getMessage();
        }
    }

    @ShellMethod(key = "preguntar", value = "Hace una pregunta sobre los artículos PDF indexados")
    public String ask(@ShellOption(help = "Pregunta en lenguaje natural") String consulta) {
        try {
            return ragService.askQuestion(consulta);
        } catch (Exception e) {
            return "Error al procesar la consulta: " + e.getMessage();
        }
    }
}