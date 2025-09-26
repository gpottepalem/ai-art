package com.giri.aiart.ollama;

import reactor.core.publisher.Flux;

public interface OllamaService {

    Flux<String> generateTextStream(String message);

    Flux<String> generateJsonStream(String message);

    Flux<String> assist(String user, String message);
}
