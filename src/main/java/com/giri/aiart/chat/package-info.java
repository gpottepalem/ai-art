@ApplicationModule(
    allowedDependencies = {"domain::type", "domain"} // Grants access to the named interface
)
package com.giri.aiart.chat;

import org.springframework.modulith.ApplicationModule;
