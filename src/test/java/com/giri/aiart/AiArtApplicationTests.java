package com.giri.aiart;

import com.giri.aiart.chat.ArtMasterService;
import com.giri.aiart.domain.type.ArtForm;
import com.giri.aiart.domain.type.ArtMedia;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

/// Basic integration test, ensures certain basic things work.
/// @author Giri Pottepalem
@SpringBootTest
class AiArtApplicationTests {
    @MockitoBean
    private ArtMasterService artMasterService;

    /// A simple yet very useful integration smoke-test-case for the application.
    /// Ensures that the application context loads successfully. If there are any issues this test fails.
	@Test
	void smokeTest_contextLoads() {
	}

    @Test
    void mocking_works() {
        var expectedResponse = "Start by learning drawing lines and curves.";
        given(artMasterService.artChatContent(ArtMedia.OIL, "Bird")).willReturn(expectedResponse);
        assertThat(artMasterService.artChatContent(ArtMedia.OIL, "Bird")).isEqualTo(expectedResponse);
    }
}
