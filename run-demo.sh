#!/bin/bash

# Oracle JDK to Eclipse Temurin Migration Demo Runner
# This script demonstrates running the application with various JVM options

echo "=== Oracle JDK to Eclipse Temurin Migration Demo ==="
echo "Current Java version:"
java -version
echo ""

# Build the application
echo "Building the application..."
mvn clean package -q

if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

echo "Build successful!"
echo ""

# Function to run the application with specific options
run_demo() {
    local TITLE="$1"
    local JVM_OPTS="$2"
    
    echo "--- $TITLE ---"
    echo "JVM Options: $JVM_OPTS"
    echo ""
    
    java $JVM_OPTS -cp target/java-modern-test-1.0-SNAPSHOT-jar-with-dependencies.jar \
        com.example.app.HelloWorldApp
    
    echo ""
    echo "Press Enter to continue to next demo..."
    read
}

# Demo 1: Basic run with minimal options (includes Oracle font APIs)
run_demo "Basic Demo" "-Xms128m -Xmx512m --add-exports java.desktop/sun.font=ALL-UNNAMED"

# Demo 2: With JFR enabled (includes Oracle font APIs)
run_demo "Java Flight Recorder Demo" \
    "-Xms256m -Xmx1g -XX:+FlightRecorder -XX:StartFlightRecording=duration=30s,filename=demo.jfr --add-exports java.desktop/sun.font=ALL-UNNAMED"

# Demo 3: With G1 garbage collector (includes Oracle font APIs)
run_demo "G1 Garbage Collector Demo" \
    "-Xms256m -Xmx1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+PrintGC --add-exports java.desktop/sun.font=ALL-UNNAMED"

# Demo 4: With comprehensive monitoring (includes Oracle font APIs)
run_demo "Comprehensive Monitoring Demo" \
    "-Xms256m -Xmx1g -XX:+UseG1GC -XX:+FlightRecorder -XX:+UnlockDiagnosticVMOptions -XX:+PrintGCDetails --add-exports java.desktop/sun.font=ALL-UNNAMED"

# Demo 5: With security policy (includes Oracle font APIs)
run_demo "Security Policy Demo" \
    "-Xms256m -Xmx1g -Djava.security.manager -Djava.security.policy=app.policy --add-exports java.desktop/sun.font=ALL-UNNAMED"

echo "=== Demo Complete ==="
echo "Check the generated .jfr files for flight recorder data"
echo "Use 'jcmd' to inspect the running JVM if needed"
