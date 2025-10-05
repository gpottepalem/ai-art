package com.giri.aiart.media;

import com.giri.aiart.prompt.PromptType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.IOException;

/**
 * Media Controller - demonstrates media-to-text in Art with AI
 *
 * @author Giri Pottepalem
 * @since v1.0
 */
@Slf4j
@RestController
@AllArgsConstructor
public class MediaController {
    private final MediaService mediaService;

    /**
     * /media-analyze
     * @apiNote
     * Ask AI to analyze an Art media.
     * <br/><b>HTTPie request examples:</b>
     * <pre><code> http --stream :8080/media-analyze</code></pre> - (defaults [media: london-boris.jpg, promptType:DESCRIPTION) describe the default media file and describe
     * <pre><code> http --stream :8080/media-analyze promptType='CAPTION'</code></pre> - generate caption for the default media file
     * <pre><code> http --stream :8080/media-analyze media=='multimodal.test.png.jpg'</code></pre> - describe this specific media file
     * <pre><code> http --stream :8080/media-analyze media=='multimodal.test.png.jpg' promptType='CAPTION'</code></pre> - generate caption for this file
     *
     * @param httpServletRequest the HttpServletRequest injected
     * @param mediaFileName the media filename of Art, defaults to london-boris.jpg
     * @param promptType the prompt type, defaults to {@link PromptType#DESCRIPTION}
     * @since v1.0
     */
    @GetMapping("/media-analyze")
    public Flux<String> analyzeMedia(
            HttpServletRequest httpServletRequest,
            @RequestParam(value = "media", required = false, defaultValue = "london-boris.jpg") String mediaFileName,
            @RequestParam(value = "promptType", required = false, defaultValue = "DESCRIPTION") PromptType promptType
    ) throws IOException {
        log.info(httpServletRequest.getRequestURL().toString());
        log.info("Analyzing media: {} for {}...", mediaFileName, promptType);

        return mediaService.analyzeMedia(mediaFileName, promptType);
    }

    /**
     * /art-master
     * @apiNote
     * Ask AI to describe an Art media.
     * <br/><b>HTTPie request:</b>
     * <pre><code> http --stream :8080/media-extract-text</code></pre> - extract text (defaults [media: gandhi-quote.png])
     * <pre><code> http --stream :8080/media-extract-text media=='giri-quote.jpg'</code></pre>
     *
     * @param httpServletRequest the HttpServletRequest injected
     * @param mediaFileName the media filename of Art, defaults to gandhi-quote.png
     * @since v1.0
     */
    @GetMapping("/media-extract-text")
    public Flux<String> extractText(
            HttpServletRequest httpServletRequest,
            @RequestParam(value = "media", required = false, defaultValue = "gandhi-quote.png") String mediaFileName
    ) throws IOException {
        log.info(httpServletRequest.getRequestURL().toString());
        log.info("Extracting text: {}...", mediaFileName);
        return mediaService.extractText(mediaFileName);
    }
}
