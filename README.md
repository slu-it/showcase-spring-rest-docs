# Showcase: Spring REST Docs
Showcase demonstrating how Spring REST Docs can be used to document parts of
your API based on your already existing tests.

This project includes:

- An example for Gradle based on Spring Boot 1.5.6
- An example for Maven based on Spring Boot 1.5.6

All of these examples package the documentation with the generated `JAR` file.
If you run the application from the `JAR`, it will expose the documentation
under `/docs/index.html`.

> __Important for Spring Boot 2.x__: The Gradle setup must be slightly changed
if you are using Spring Boot 2 (currently only availabe as milestones). Instead
of the `jar` task, you need to modify the new `bootJar` task. The configuration
content is the same!
