package com.giri.aiart;

import com.giri.aiart.chat.ArtMasterService;
import com.giri.aiart.modules.artist.service.ArtistService;
import com.giri.aiart.shared.domain.type.ArtMedia;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

/// Integration Smoke Test to ensure that the application starts up.
/// @author Giri Pottepalem
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    useMainMethod = SpringBootTest.UseMainMethod.ALWAYS
)
@ExtendWith(OutputCaptureExtension.class)
@Import(TestcontainersConfig.class)
class AiArtApplicationIntegrationTest {
    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    ArtistService artistService;

    @MockitoBean
    private ArtMasterService artMasterService;

    /// A simple yet very useful integration smoke-test-case for the application.
    /// Ensures that the application context loads successfully. If there are any issues this test fails.
	@Test
	void smokeTest_contextLoads_and_autowiring_works(CapturedOutput capturedOutput) {
        var service = applicationContext.getBean(ArtMasterService.class);

        assertThat(service).isNotNull();
        assertThat(artistService).isInstanceOf(ArtistService.class);
        assertThat(artMasterService).isNotNull();

        assertThat(capturedOutput).contains("Database version: PostgreSQL 18.0");
	}

    @Test
    void mocking_works() {
        var expectedResponse = "Start by learning drawing lines and curves.";
        given(artMasterService.artChatContent(ArtMedia.OIL, "Bird")).willReturn(expectedResponse);
        assertThat(artMasterService.artChatContent(ArtMedia.OIL, "Bird")).isEqualTo(expectedResponse);
    }
}
