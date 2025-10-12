# Maven commands
## Build

### Run
```shell
./mvnw clean
./mvnw clean spring-boot:run
```

### Run unit tests
[TODO]
### Run integration tests
[TODO]

### Run specific test
[TODO]

### Run specific test-method
[TODO]

## Investigation

### Dependencies
```shell
./mvnw dependency:tree
```

### Debugging Maven Plugin Warnings
To trace the source of Maven plugin warnings (such as deprecated configurations), you can inspect the 
**effective POM** — which shows the final configuration after inheritance and profile merging.
To track down any Maven plugin warnings/issues, run the following command and search for the warning/issue.
```shell
./mvn help:effective-pom
```
Then search the output for the warning or configuration you're investigating (e.g., `systemProperties`, 
plugin versions, inherited settings, etc.).
