package com.example.app;

import jdk.jfr.Configuration;
import jdk.jfr.Recording;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple Hello World application that demonstrates the use of Java Flight Recorder (JFR),
 * which is a feature that was originally exclusive to Oracle JDK but is now available
 * in the free Oracle OpenJDK as well.
 */
public class HelloWorldApp {
    public static void main(String[] args) {
        System.out.println("Hello, Oracle JDK World!");

        try {
            // Start a JFR recording - a feature that was exclusive to Oracle JDK
            // but is now part of the OpenJDK as well
            Path recordingPath = Paths.get("hello_world_recording.jfr");
            
            // Get the default configuration
            Configuration config = Configuration.getConfiguration("default");
            
            // Create and start a recording
            Recording recording = new Recording(config);
            recording.setDuration(Duration.ofSeconds(5));
            recording.setName("HelloWorldRecording");
            System.out.println("Starting JFR recording...");
            recording.start();
            
            // Do some work
            System.out.println("Doing some computation...");
            for (int i = 0; i < 10; i++) {
                doSomeWork();
            }
            
            // Stop the recording and dump to file
            recording.stop();
            recording.dump(recordingPath);
            System.out.println("JFR recording saved to: " + recordingPath.toAbsolutePath());
            
            // Read and analyze the recording (simple example)
            readRecording(recordingPath);

        } catch (Exception e) {
            System.err.println("Error with JFR recording: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void doSomeWork() {
        // Just a sample computation
        long sum = 0;
        for (long i = 0; i < 1_000_000; i++) {
            sum += i;
        }
        System.out.println("Computation result: " + sum);
    }
    
    private static void readRecording(Path path) {
        try {
            System.out.println("Analyzing recording...");
            List<RecordedEvent> events = RecordingFile.readAllEvents(path);
            
            // Print some basic info about the events captured
            System.out.println("Total events recorded: " + events.size());
            
            // Group events by name and count
            var eventCounts = events.stream()
                    .collect(Collectors.groupingBy(RecordedEvent::getEventType, Collectors.counting()));
            
            System.out.println("Event types recorded:");
            eventCounts.forEach((type, count) -> 
                System.out.println(" - " + type.getName() + ": " + count));
                
        } catch (Exception e) {
            System.err.println("Error reading JFR recording: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
