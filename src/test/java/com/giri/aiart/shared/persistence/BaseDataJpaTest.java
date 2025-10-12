package com.giri.aiart.shared.persistence;

import com.giri.aiart.TestcontainersConfig;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

/// Base Data Jpa Test.
/// Light-weight Jpa integration test for persistence layer.
/// **NOTE:** Not a full-blown @SpringBootTest.
///
/// @author Giri Pottepalem
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@Import({TestcontainersConfig.class})
public abstract class BaseDataJpaTest {
}
