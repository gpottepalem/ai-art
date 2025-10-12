package com.giri.aiart;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/// TODO
///
/// @author Giri Pottepalem
@ActiveProfiles("test")
@SpringBootTest
@Import(TestcontainersConfig.class)
public abstract class BaseIntegrationTest {

    // Testcontainers will automatically start the container defined in TestcontainersConfig
    // and provide a dynamic property source for the datasource.
}
