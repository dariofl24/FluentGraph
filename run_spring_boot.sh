#!/bin/bash

# Check if Java is installed
if type -p java >/dev/null 2>&1; then
    # Check Java version
    version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    if [[ "$version" < "1.8" ]]; then
        echo "Java version is too old, please install Java 1.8 or greater"
    else
        echo "Java is installed and the version is $version"
        # Run the Spring Boot application
        ./mvnw spring-boot:run &
        # Open the web browser to localhost:8080
        if [[ "$(uname)" == "Darwin" ]]; then
            open "http://localhost:8080"
        else
            xdg-open "http://localhost:8080"
        fi
    fi
else
    echo "Java is not installed"
fi

