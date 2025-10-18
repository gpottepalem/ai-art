/// Exposes this sub-module package (domain::dto) to other modules.
/// Modules that need access this package needs to add the following to their package-info.java
/// ```java
/// @ApplicationModule(
///     allowedDependencies = "domain::dto" // Grants access to the named interface
/// )```
@org.springframework.modulith.NamedInterface("dto")
package com.giri.aiart.shared.dto;
