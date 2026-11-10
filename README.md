# PdfRAGAgent

Aplicación de línea de comandos para indexar artículos en PDF y consultarlos mediante RAG. Extrae el texto de cada página, lo divide en fragmentos, genera embeddings con Ollama y los almacena en MongoDB Atlas. Al responder, recupera los cuatro fragmentos más similares y los utiliza como contexto para el modelo de chat.

## Requisitos

- Java 17
- MongoDB Atlas con Vector Search
- [Ollama](https://ollama.com/) en ejecución en `http://localhost:11434`
- Los modelos `llama3` y `nomic-embed-text` disponibles en Ollama

Descarga los modelos antes de iniciar la aplicación:

```bash
ollama pull llama3
ollama pull nomic-embed-text
```

## Configuración de MongoDB

La URI de MongoDB se lee desde la variable de entorno `MONGODB_URI`. No guardes credenciales en este repositorio. Configúrala en la terminal antes de ejecutar la aplicación:

```bash
export MONGODB_URI='mongodb+srv://<usuario>:<password>@<cluster>/<base-de-datos>?retryWrites=true&w=majority'
```

Reemplaza los valores entre `<...>` por los datos de tu clúster de MongoDB Atlas. En IntelliJ, agrega `MONGODB_URI` a las variables de entorno de la configuración de ejecución.

La aplicación inicializa el almacén vectorial MongoDB usando la colección `article_embeddings` y el índice `vector_index`.

## Ejecución

Desde la raíz del proyecto:

```bash
./mvnw spring-boot:run
```

La aplicación se inicia en modo interactivo de Spring Shell.

## Uso

Indexa un PDF indicando su ruta absoluta o relativa:

```text
cargar-pdf /ruta/al/articulo.pdf
```

El texto extraído se divide en fragmentos y se guarda como vectores en MongoDB Atlas. Luego consulta los documentos indexados en lenguaje natural:

```text
preguntar ¿Qué enfoque propone el artículo?
```

La respuesta se basa únicamente en el contexto recuperado de los PDF indexados. Si no encuentra información suficiente en esos documentos, el asistente lo indicará.
