# Samurai-Ramen

## Overview

Samurai Ramen is a JavaFX-based restaurant ordering system built in a Maven project. It provides a graphical interface for browsing menu categories, selecting ramen, meals, desserts, and beverages, and generating order invoices.

## Technology Stack

- Java 23
- JavaFX 17
- Maven
- IntelliJ IDEA
- ControlsFX
- FormsFX
- BootstrapFX
- iText PDF for invoice generation

## Project Structure

- `CCE104_Ramen-master/`
  - `pom.xml` — Maven configuration
  - `src/main/java/com/example/cce104_ramen/` — Java source files
  - `src/main/resources/com/example/cce104_ramen/` — FXML, CSS, images, and assets
  - `module-info.java` — Java module configuration

## Running in IntelliJ

1. Open IntelliJ IDEA.
2. Choose `Open` and select the `CCE104_Ramen-master` folder.
3. Make sure the project SDK is set to Java 23.
4. Allow IntelliJ to import the Maven project.
5. Create a new run configuration for the main class:
   - Main class: `com.example.cce104_ramen.Home`
6. Run the application.

## Notes

- The application entry point is `Home.java`.
- If you need to run from Maven or from the command line, ensure the JavaFX SDK is configured properly in your environment.

## Contact

If you want to improve the README, add instructions for building distributable artifacts or updating the JavaFX runtime configuration.