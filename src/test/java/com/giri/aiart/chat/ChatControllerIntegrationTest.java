package com.giri.aiart.chat;

import com.giri.aiart.domain.type.ArtMedia;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ApplicationModuleTest //(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatControllerIntegrationTest {
    @Autowired
    ArtMasterController artMasterController;

    @MockitoBean
    ArtMasterService factService;

    @Test
    void artChatContent_returns_chatContent() {
        // given:
        String object = "Bird";
        String expectedResponse = "Start by learning drawing lines and curves.";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/chat");

        when(factService.artChatContent(ArtMedia.PENCIL, object)).thenReturn(expectedResponse);

        // when:
        var responseEntity = artMasterController.artChatContent(request, ArtMedia.PENCIL, object);

        // verify:
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.entity()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getResponse()).isEqualTo(expectedResponse);
    }

    @Test
    void artChatResponse_returns_ChatResponse() {
        // given:
        String object = "Bird";
        String expectedResponse = "Start by learning drawing lines and curves.";
        ChatResponse expected = mock(ChatResponse.class);
        when(factService.artChatResponse(ArtMedia.PENCIL, object)).thenReturn(expected);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/chat/raw");

        // when:
        var responseEntity = artMasterController.artChatResponse(request, ArtMedia.PENCIL, object);

        // verify:
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.entity()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getResponse()).isEqualTo(expected);
    }

    @Test
    void artChatClientResponse_returns_ChatClientResponse() {
        // given:
        // given:
        String object = "Bird";
        String expectedResponse = "Start by learning drawing lines and curves.";
        ChatClientResponse expected = mock(ChatClientResponse.class);
        when(factService.artChatClientResponse(ArtMedia.PENCIL, object)).thenReturn(expected);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/chat/raw/client");

        // when:
        var responseEntity = artMasterController.artChatClientResponse(request, ArtMedia.PENCIL, object);

        // verify:
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.entity()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getResponse()).isEqualTo(expected);
    }

    @Test
    void artStreamChatContent_returns_chat_content_stream() {
        // given:
        String object = "Bird";
        String expectedResponse = "Start by learning drawing lines and curves.";
        Flux<String> expectedResponseFlux = Flux.just("Start", "by", "learning", "drawing", "lines", "and", "curves", ".");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/stream");

        when(factService.artStreamChatContent(ArtMedia.PENCIL, object)).thenReturn(expectedResponseFlux);

        // when:
        var chatStream = artMasterController.artStreamChatContent(request, ArtMedia.PENCIL, object);

        // verify:
        assertThat(chatStream).isNotNull();
        assertThat(chatStream).isEqualTo(expectedResponseFlux);
    }

    @Test
    void artStreamChatContentWords_returns_chat_content_stream_of_words() {
        String object = "Bird";
        String expectedResponse = "Start by learning drawing lines and curves.";
        Flux<String> expectedResponseFlux = Flux.just("Start", "by", "learning", "drawing", "lines", "and", "curves", ".");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/stream/words");

        when(factService.artStreamChatContentWords(ArtMedia.PENCIL, object)).thenReturn(expectedResponseFlux);

        // when:
        var chatStream = artMasterController.artStreamChatContentWords(request, ArtMedia.PENCIL, object);

        // verify:
        assertThat(chatStream).isNotNull();
        assertThat(chatStream).isEqualTo(expectedResponseFlux);
    }
}
