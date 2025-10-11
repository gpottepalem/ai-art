/// Exposes this sub-module package (domain::type) to other modules.
/// Modules that need access this package needs to add the following to their package-info.java
/// ```java
/// @ApplicationModule(
///     allowedDependencies = "domain::type" // Grants access to the named interface
/// )```
@org.springframework.modulith.NamedInterface("domain")
package com.giri.aiart.shared.domain;
