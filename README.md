# ai-art
Lets explore what AI can bring in to my Art World.

Check [Wiki Pages](https://github.com/gpottepalem/ai-art/wiki) for additional details.

## Prerequisites
* Make sure you have [Java 25](https://www.java.com/en/) installed. [SDK-MAN](https://sdkman.io/) is your companion for managing multiple Java
  versions.
* Make sure you have [Docker](https://www.docker.com/) installed and running.

## [Maven Commands](./MAVEN.md)

### Running the app
Run the following command from the project root dir:
```shell
./mvnw spring-boot:run
```
### Modularity - [Spring Modulith](https://docs.spring.io/spring-modulith/reference/)

* Make sure you have `graphviz` installed
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

## Docker Compose
* Check Docker desktop, you will have ai-art docker compose and under that the following containers up and running:
  * ollama
  * llava.
  * pgvector
  * zipkin

## PGVector
Vector DB

## API end-points

### /chat
[Check Actuator Mappings](http://localhost:8080/actuator/mappings) and look for /chat
Open a terminal or browse  
* http :8080/chat (http://localhost:8080/chat)  
* http :8080/chat/stream (http://localhost:8080/chat/stream)  
* http :8080/chat/stream/words  (http://localhost:8080/chat/stream/words)  
* http --stream GET :8080/chat/stream/words query=="Can you name one famous Artist from India?"  
(http://localhost:8080/chat/stream/words?query=Can%20you%20name%20one%20famous%20Artist%20from%20India%3F)  

### /art
* http :8080/art (http://localhost:8080/ai/art)
* http --stream :8080/ai/joke/stream (http://localhost:8080/ai/joke/stream)  
* http --stream :8080/ai/joke/stream/json (http://localhost:8080/ai/joke/stream/json)


## Testing APIs
* Postman doesn't support streaming.
* Use either browser, or httpie/curl with stream option  
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

### Zipkin - Distributed Tracing
* Zipkin is added for a deeper traceability.
* The Docker Compose file from Spring Initializr starts a Zipkin server at [http://localhost:9411](http://localhost:9411).
* Make a few API requests and open Zipkin UI to check traces.

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

### Actuator - AI 
* How many times API is called - [http://localhost:8080/actuator/metrics/gen_ai.client.operation](http://localhost:8080/actuator/metrics/gen_ai.client.operation)
* Client operation active - [http://localhost:8080/actuator/metrics/gen_ai.client.operation.active](http://localhost:8080/actuator/metrics/gen_ai.client.operation.active)  
* Token Usage - [http://localhost:8080/actuator/metrics/gen_ai.client.token.usage](http://localhost:8080/actuator/metrics/gen_ai.client.token.usage)
