package com.giri.aiart;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/// Spring Modularity tests
/// @author Giri Pottepalem
public class ModularityTest {
    ApplicationModules modules = ApplicationModules.of(AiArtApplication.class);

    /// Verify application structure. Rejects cyclic dependencies and access to internal types.
    @Test
    void verify_modularity_structure() {
        System.out.println(modules);
        modules.verify();
    }

    /// Generate Application Module Component diagrams under target/spring-modulith-docs.
    @Test
    void create_module_documentation() {
        new Documenter(modules)
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml();
    }
}
