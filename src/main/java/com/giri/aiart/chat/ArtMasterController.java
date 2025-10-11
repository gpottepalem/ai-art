package com.giri.aiart.chat;

import com.giri.aiart.shared.domain.Painting;
import com.giri.aiart.shared.domain.type.ArtMedia;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.ResponseEntity;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Art Master Controller - demonstrates chatting about learning Art with AI
 *
 * @author Giri Pottepalem
 * @since v1.0
 */
@RestController
@RequestMapping("/art-master")
@Slf4j
public class ArtMasterController {
    private final ArtMasterService artMasterService;

    public ArtMasterController(ArtMasterService chatClientService) {
        this.artMasterService = chatClientService;
    }

    /**
     * /art-master
     * @apiNote
     * Ask AI Art Master how learn any kind of Art, e.g. OIL Bird
     * <br/><b>HTTPie request:</b>
     * <pre><code> http :8080/art-master media=='WATERCOLOR' type=='Human Face'</code></pre>
     *
     * @param request the HttpServletRequest injected
     * @param artMedia art media
     * @param object the object
     * @since v1.0
     */
    @GetMapping
    public ResponseEntity<String, HttpStatus> artChatContent(
            HttpServletRequest request,
            @RequestParam(value = "media", required = false, defaultValue = "OIL") ArtMedia artMedia,
            @RequestParam(value = "object", required = false, defaultValue = "Bird") String object) {
        log.info(request.getRequestURL().toString());
        String response = artMasterService.artChatContent(artMedia, object);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /**
     * /art-master/stream
     * @apiNote
     * Ask AI Art Master how learn any kind of Art, e.g. OIL Bird.
     * You will get the streaming response.
     * <br/><b>HTTPie request:</b>
     * <pre><code> http --stream :8080/art-master/stream media=='WATERCOLOR' object=='Human Face'</code></pre>
     *
     * @param artMedia art media
     * @param object the object
     * @since v1.0
     */
    @GetMapping("/stream")
    public Flux<String> artStreamChatContent(
            HttpServletRequest request,
            @RequestParam(value = "media", required = false, defaultValue = "OIL") ArtMedia artMedia,
            @RequestParam(value = "object", required = false, defaultValue = "Bird") String object) {
        log.info(request.getRequestURL().toString());
        return artMasterService.artStreamChatContent(artMedia, object);
    }

    /**
     * /art-master/raw
     * @apiNote
     * Ask AI Art Master how learn any kind of Art, e.g. OIL Bird.
     * You will get the raw response - good for examining tokens etc.
     * <br/><b>HTTPie request:</b>
     * <pre><code> http :8080/art-master/raw media=='WATERCOLOR' object=='Human face'</code></pre>
     *
     * @param artMedia art media
     * @param object the object
     * @since v1.0
     */
    @GetMapping("/raw")
    public ResponseEntity<ChatResponse, HttpStatus> artChatResponse(
            HttpServletRequest request,
            @RequestParam(value = "media", required = false, defaultValue = "OIL") ArtMedia artMedia,
            @RequestParam(value = "object", required = false, defaultValue = "Bird") String object) {
        log.info(request.getRequestURL().toString());
        var response = artMasterService.artChatResponse(artMedia, object);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * /art-master/raw/client
     * @apiNote
     * Ask AI Art Master how learn any kind of Art, e.g. OIL Bird.
     * You will get the raw response - good for examining tokens etc.
     * <br/><b>HTTPie request:</b>
     * <pre><code> http :8080/art-master/raw/stream media=='WATERCOLOR' object=='Human face'</code></pre>
     *
     * @param artMedia art media
     * @param object the object
     * @since v1.0
     */
    @GetMapping("/raw/client")
    public ResponseEntity<ChatClientResponse, HttpStatus> artChatClientResponse(
            HttpServletRequest request,
            @RequestParam(value = "media", required = false, defaultValue = "OIL") ArtMedia artMedia,
            @RequestParam(value = "object", required = false, defaultValue = "Bird") String object) {
        log.info(request.getRequestURL().toString());
        var response = artMasterService.artChatClientResponse(artMedia, object);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * /art-master/raw/stream
     * @apiNote
     * Ask AI Art Master how learn any kind of Art, e.g. OIL Bird.
     * You will get the streaming raw response - good for examining tokens etc.
     * <br/><b>HTTPie request:</b>
     * <pre><code> http :8080/art-master/raw/stream media=='WATERCOLOR' object=='Human face'</code></pre>
     *
     * @param artMedia art media
     * @param object the object
     * @since v1.0
     */
    @GetMapping("/raw/stream")
    public Flux<ChatResponse> artStreamChatResponse(
            HttpServletRequest request,
            @RequestParam(value = "media", required = false, defaultValue = "OIL") ArtMedia artMedia,
            @RequestParam(value = "object", required = false, defaultValue = "Bird") String object) {
        log.info(request.getRequestURL().toString());
        return artMasterService.artStreamChatResponse(artMedia, object);
    }

    /**
     * /art-master/words/stream
     * @apiNote
     * Ask AI Art Master how learn any kind of Art, e.g. OIL Bird.
     * You will get the streaming response with output stream of words.
     * <br/><b>HTTPie request:</b>
     * <pre><code>http --stream :8080/art-master/stream/words media=='WATERCOLOR' object=='Human face'</code></pre>
     *
     * @param artMedia art media
     * @param object the object
     * @since v1.0
     */
    @GetMapping("/words/stream")
    public Flux<String> artStreamChatContentWords(
            HttpServletRequest request,
            @RequestParam(value = "media", required = false, defaultValue = "OIL") ArtMedia artMedia,
            @RequestParam(value = "object", required = false, defaultValue = "Bird") String object) {
        log.info(request.getRequestURL().toString());
        return artMasterService.artStreamChatContentWords(artMedia, object);
    }

    /**
     * /art-master/paintings
     * @apiNote
     * Ask AI for an Artist's paintings(entities as JSON array).
     * @param about the about to ask e.g. Give me 5 famous paintings by Picasso.
     * @return the {@link Painting} entity
     */
    @GetMapping("/paintings")
    public List<Painting> synchronousChatEntity(
            HttpServletRequest request,
            @RequestParam(value = "about", required = false, defaultValue = "Give me 3 famous paintings by Picasso.") String about) {
        log.info(request.getRequestURL().toString());
        return artMasterService.synchronousChatEntity(about);
    }

    /**
     * /art-master/paintings/stream
     * @apiNote
     * Ask AI for an Artist's stream of paintings(entities as JSON array).
     * @param about the about to ask e.g. Give me 5 famous paintings by Picasso.
     * @return the {@link Painting} entity
     */
    @GetMapping(value = "/paintings/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<Painting>> streamingEntityCollection(
            HttpServletRequest request,
            @RequestParam(value = "about", required = false, defaultValue = "Give me 3 famous paintings by Picasso.") String about) {
        log.info(request.getRequestURL().toString());
        return artMasterService.streamingEntityCollection(about);
    }
}
