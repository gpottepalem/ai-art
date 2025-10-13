package com.giri.aiart.ollama;

import com.giri.aiart.shared.util.LogIcons;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/art")
@Slf4j
class OllamaController {
    private OllamaService ollamaService;

    OllamaController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    // With system prompt/recipe
    @GetMapping("/")
    public Flux<String> generate(@RequestParam(value = "message", defaultValue = "In which country Art is valued most in th world? Just give me answer in one sentence.") String message,
                                 HttpServletRequest httpServletRequest) {
        log.info("{} {}", LogIcons.CONTROLLER, httpServletRequest.getRequestURL().toString());
        return ollamaService.generateTextStream(message);
    }

    // http :8080/ai/stream?message="Tell me a joke"
    @GetMapping("/joke/stream")
    public Flux<String> stream(@RequestParam(value = "message", defaultValue = "Tell me an Art related joke") String message,
                               HttpServletRequest httpServletRequest) {
        log.info("{} {}", LogIcons.CONTROLLER, httpServletRequest.getRequestURL().toString());
        return ollamaService.generateTextStream(message);
    }

    // http :8080/ai/stream/json?message="Tell me a joke"
    @GetMapping("/joke/stream/json")
    public Flux<String> streamJson(@RequestParam(value = "message", defaultValue = "Tell me an Art related joke") String message,
                                   HttpServletRequest httpServletRequest) {
        log.info("{} {}", LogIcons.CONTROLLER, httpServletRequest.getRequestURL().toString());
        return ollamaService.generateJsonStream(message);
    }

    @GetMapping("{user}/assistant")
    public Flux<String> inquire(@PathVariable String user, @RequestParam String question,
                                HttpServletRequest httpServletRequest) {
        log.info("{} {}", LogIcons.CONTROLLER, httpServletRequest.getRequestURL().toString());
        return ollamaService.assist(user, question);
    }
}
