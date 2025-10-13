/// Exposes this sub-module package (util) to other modules.
/// Modules that need access this package needs to add the following to their package-info.java
/// ```java
/// @ApplicationModule(
///     allowedDependencies = "util" // Grants access to the named interface
/// )```
@org.springframework.modulith.NamedInterface("util")
package com.giri.aiart.shared.util;
