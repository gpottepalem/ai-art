# aiArt
AI in Art

# Ollama
## [Installation](https://gptforwork.com/help/ai-models/custom-endpoints/set-up-ollama-on-macos)
[Download](https://ollama.com/download) and install Ollama.
Run Ollama from Applications.

Verify
```
$ curl http://localhost:11434
Ollama is running
```
### Install & Run Mistral
```
$ ollama list
NAME    ID    SIZE    MODIFIED
$ ollama pull mistral
...
success
$ ollama run mistral
>>> My name is Giri
 Hello Giri! How can I help you today? Is there a specific question or topic you'd like to discuss or learn about? I'm here to provide information, answer questions, and engage
in friendly conversation. Let me know if you have any questions related to technology, science, history, or anything else that interests you.

>>> /?     /// Help
>>> /bye   /// Quit
$
$ ollama run deepseek-r1:latest
```
Mistral
Mistral is one of the many [models supported by Ollama](https://ollama.com/search).

The above running mistral won't help Spring Boot error.

## [Spring AI and Ollama](https://spring.io/blog/2024/10/22/leverage-the-power-of-45k-free-hugging-face-models-with-spring-ai-and-ollama)

ERROR
```
org.springframework.ai.retry.NonTransientAiException: HTTP 404 - {"error":"model \"mistral\" not found, try pulling it first"}]
```

The Spring AI Ollama integration can automatically pull unavailable models for both chat completion and embedding models.
The docker compose will start ollama and Spring boot pulls Mistral the very first time, the pull implicitly runs it too.  
Add the following properties in application.properties:
```
# ollama
spring.ai.ollama.init.pull-model-strategy=always
spring.ai.ollama.init.timeout=60s
spring.ai.ollama.init.max-retries=1
```

- [Ollama API](https://github.com/ollama/ollama/blob/main/docs/api.md#generate-embeddings)  
Examples
```shell

```
### [Ollama embeddings](https://docs.spring.io/spring-ai/reference/api/embeddings/ollama-embeddings.html)
With Ollama you can not only various models locally, but also generated embeddings from them.
An embedding is a vector (list) of floating point numbers. The distance between two vectors 
measures their relatedness. Small distances suggest high relatedness and large distances suggest 
low relatedness.  
[Embeddings API](https://github.com/ollama/ollama/blob/main/docs/api.md#generate-embeddings)  
E.g.
Check your Docker for Ollama running port (random) 50071:11434. http://localhost:50071/ 
```shell
/// curl
$ curl http://localhost:50071/api/embed -d '{
  "model": "mistral",
  "input": "Why is the sky blue?"
}'

/// httpie
$ http POST :50071/api/embed model=mistral input="Why is the sky blue?"
/// using json block
$ http POST :50071/api/embed \
    Content-Type:application/json \
    <<< '{"model": "mistral", "input": "Why is the sky blue?"}'

```

## Streaming Response
`ERROR: java.lang.IllegalStateException: No primary or single unique constructor found for interface org.springframework.web.server.ServerWebExchange`
- You cannot have both web and web-flux dependencies. web-flux `ServerWebExchange` is like web `HttpServletRequest`
- Generate a response for a given prompt with a provided model. This is a streaming endpoint, so there will be a series
  of responses. The final response object will include statistics and additional data from the request.


## Run application
Make sure you have Docker running.  
Run the application.  
Check Docker desktop, you will have aiart docker compose and under that the pgvector, zipkin, ollama containers running.

## Available API end-points
### /chat
[Check Actuator Mappings](http://localhost:8080/actuator/mappings) and look for /chat
Open a terminal or browse  
. http :8080/chat (http://localhost:8080/chat)  
. http :8080/chat/stream (http://localhost:8080/chat/stream)  
. http :8080/chat/stream/words  (http://localhost:8080/chat/stream/words)  
. http --stream GET :8080/chat/stream/words query=="Can you name one famous Artist from India?"  
(http://localhost:8080/chat/stream/words?query=Can%20you%20name%20one%20famous%20Artist%20from%20India%3F)  

### /art
. http :8080/art (http://localhost:8080/ai/art)
. http --stream :8080/ai/joke/stream (http://localhost:8080/ai/joke/stream)  
. http --stream :8080/ai/joke/stream/json (http://localhost:8080/ai/joke/stream/json)  

## Documentation
### Modularity - [Spring Modulith](https://docs.spring.io/spring-modulith/reference/)
. Make sure you have `graphviz` installed
```shell
brew install graphviz
```
. Install IntelliJ IDEA's [PlantUML Integration plugin](https://plugins.jetbrains.com/plugin/7017-plantuml4idea)
. Run test-case: ModularityTest which generates modulith PlantUML files (.puml) under application's target/spring-modulith-docs directory.
. When you open any .puml file generated in IntelliJ, the plugin shows it as PlantUML diagram.

### [API Docs](https://smart-doc-group.github.io/)
API documentation is generated using [smart-doc](https://smart-doc-group.github.io/) generated under `src/main/resources/static/doc`  
Check this link: [http://localhost:63342/aiArt/aiart/static/doc/api.html](http://localhost:63342/aiArt/aiart/static/doc/api.html)

Run the following command to generate APT docs and check API doc under `src/main/resources/static/doc` (path configured in `smart-doc.json`)
```
./mvnw smart-doc:html
```

# [Maven Commands](./MAVEN.md)


## Testing APIs
- Postman doesn't support streaming.
- Use either browser, or httpie/curl with stream option  
Httpie supports with --stream option.  
e.g.
```shell
# Httpie with stream option for streaming end-point
http :8080/ai/stream           /// non-streaming mode
http --stream :8080/ai/stream  /// streaming mode

# curl with streaming option for streaming end-point
 curl http://localhost:8080/ai/stream                                    /// non-streaming mode
 curl -N -H "Accept: text/event-stream" http://localhost:8080/ai/stream  /// streaming mode
```

## Observability

Actuator Endpoint - [http://localhost:8080/actuator](http://localhost:8080/actuator)  
Metrics names - [http://localhost:8080/actuator/metrics](http://localhost:8080/actuator/metrics) - Notice gen_ai  
> **NOTE:**
> There must at least be one client operation performed (one Model request) for the following metrics to be
> listed in metrics names. 

Added Zipkin for a deeper traceability.
The Docker Compose file from Spring Initializr starts a Zipkin server at [http://localhost:9411](http://localhost:9411).
Make a few API requests and open Zipkin UI to check traces.

> **NOTE:**
> Zipkin configuration out of the box is set to trace only 10% of requests. So, only the first request is traced and 
subsequent requests are not traced/shown.

Add the following property to `application.properties`:
```
management.tracing.sampling.probability=1.0
```
~~~
Test Note
~~~

How many times API is called - [http://localhost:8080/actuator/metrics/gen_ai.client.operation](http://localhost:8080/actuator/metrics/gen_ai.client.operation)  
Client operation active - [http://localhost:8080/actuator/metrics/gen_ai.client.operation.active](http://localhost:8080/actuator/metrics/gen_ai.client.operation.active)  
Token Usage - [http://localhost:8080/actuator/metrics/gen_ai.client.token.usage](http://localhost:8080/actuator/metrics/gen_ai.client.token.usage)  

## Ollama API Examples
### `POST /api/generate` - [Generate a completion](https://github.com/ollama/ollama/blob/main/docs/api.md#generate-a-completion)
Generate a response for a given prompt with a provided model. This is a streaming endpoint, so there will be a series of responses. The final 
response object will include statistics and additional data from the request.

[Generate API Request Examples](OLLAMA-GENERATE.md)

### `POST /api/chat` - [Generate a chat completion](https://github.com/ollama/ollama/blob/main/docs/api.md#generate-a-chat-completion)
Generate the next message in a chat with a provided model. This is a streaming endpoint, so there will be a series of 
responses. Streaming can be disabled using "stream": false. The final response object will include statistics and 
additional data from the request.
 - chat messages need to be sent in `messages` array with 'role'.
 - tool/function calling - list of `tools` can be specified with `type` like `function`
 - structured output with `formt`

### `POST /api/create` - [Create Model](https://github.com/ollama/ollama/blob/main/docs/api.md#create-a-model)
### `GET /api/list` - [List Local Models](https://github.com/ollama/ollama/blob/main/docs/api.md#list-local-models)
`curl http://localhost:11434/api/tags`
### `POST /api/show` - [Show Model Information](https://github.com/ollama/ollama/blob/main/docs/api.md#show-model-information)
`curl http://localhost:11434/api/show`
### `GET /api/ps` - [List Running Models](https://github.com/ollama/ollama/blob/main/docs/api.md#list-running-models)
`curl http://localhost:11434/api/ps`
### `GET /api/version` - [Get Ollama Version](https://github.com/ollama/ollama/blob/main/docs/api.md#version)
`curl http://localhost:11434/api/ps`
### `POST /api/copy` - [Copy Model](https://github.com/ollama/ollama/blob/main/docs/api.md#copy-a-model)
### Delete, Pull, Push, [Generate Embeddings](https://github.com/ollama/ollama/blob/main/docs/api.md#generate-embedding),

# RAG - Retrieval Augmented Generation
Augment LLM prompts with private database content (non-public, domain specific). 
Enables LLMs to produce more accurate answers while keeping business data secure.
Vector search to find documents that closely match the user's question.
Chunking is more difficult. Quality of chunks has direct impact on the quality of the context provided to the LLM.

# Prompt Engineering
Formatting including place holders. Prompt gives extra info about the task. Significant impact on the quality of response.
Optimize for the intended use case.

# Model paramaters
Affect response created. Temperature, Top K, Top P, frequency penalty, presence penalty are considered in choosing the next token.
Maximum token controls the length of the response.

# Tools
Max and match tools.
# Chatbot app

# Context window
Context is important. Memory.
