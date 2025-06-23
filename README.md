# FluentGraph

FluentGraph is a Spring Boot application that analyses Fluent Commerce workflow JSON files and visualises how the rulesets relate to one another. Each ruleset becomes a node in a graph and the links between nodes are derived from rule references. The UI groups related nodes into clusters so that the overall workflow structure can be explored more easily.

## Features
- Import workflow definitions from JSON files
- Persist workflow data using an embedded JSONDB database
- Build graph clusters that show how rulesets interact
- Browse clusters from a web UI powered by VIS.JS
- REST endpoints to manage workflow files

## Requirements
- Java 8 or higher
- Maven 3.x
- A directory where JSONDB can store its data

## Getting Started
1. Configure the database location by editing `src/main/resources/application.properties` and setting the property `jsondb.files` to a writable directory.
2. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```
   Alternatively, execute `run_spring_boot.sh` which checks your Java version and opens the browser automatically.
3. Navigate to `http://localhost:8080/home` to access the UI.

## Usage Overview
- Add workflow files through the UI or via the REST API under `/wffile`.
- Select the workflows you wish to analyse and generate clusters.
- Click on a cluster to view the graph. Selecting a node displays the underlying ruleset.

The tool is experimental; verify the generated clusters against your workflow definitions when in doubt.

## Project Layout
- `src/main/java` – application source code
- `src/main/resources` – configuration and templates
- `documentation` – screenshots and additional documentation used in the original tutorial

## License
This project does not currently specify a license.
