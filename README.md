# Hello World Java Application with Oracle JDK Features

This is a simple Hello World Java application that demonstrates the use of Java Flight Recorder (JFR), a feature that was originally exclusive to Oracle JDK but is now available in Oracle OpenJDK.

## Prerequisites

- Oracle JDK 17 or later (OpenJDK version)
- Maven

## Building the Application

To build the application, run:

```bash
mvn clean package
```

This will create a JAR file in the `target` directory.

## Running the Application

To run the application, use:

```bash
java -jar target/java-modern-test-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Features Demonstrated

This application demonstrates:

- Java Flight Recorder (JFR) - A low-overhead data collection framework for troubleshooting Java applications
- Stream API - For processing collections of objects
- Path API - For file handling

## JFR Output

The application will create a file named `hello_world_recording.jfr` in the current directory. This file contains the recording of JVM events during the application execution.

You can analyze this file using JDK Mission Control, which is available as a separate download from Oracle, or through command line tools like:

```bash
jfr print hello_world_recording.jfr
```

Or for a summary:

```bash
jfr summary hello_world_recording.jfr
```
