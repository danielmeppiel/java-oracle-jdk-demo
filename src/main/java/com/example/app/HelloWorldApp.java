package com.example.app;

import jdk.jfr.Configuration;
import jdk.jfr.Recording;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.security.Security;

/**
 * A comprehensive Java application demonstrating Oracle JDK features
 * that require consideration when migrating to Eclipse Temurin JDK.
 * 
 * This demo includes:
 * - Java Flight Recorder (JFR) functionality
 * - JVM monitoring and management
 * - Security provider analysis
 * - Memory management insights
 * - Performance profiling
 */
public class HelloWorldApp {
    
    public static void main(String[] args) {
        System.out.println("=== Oracle JDK to Eclipse Temurin Migration Demo ===");
        System.out.println("Current JVM: " + System.getProperty("java.vendor") + " " + System.getProperty("java.version"));
        System.out.println("JVM Name: " + System.getProperty("java.vm.name"));
        System.out.println();
        
        // Run comprehensive demonstrations
        try {
            // Basic JVM analysis
            demonstrateJVMInfo();
            
            // Security and cryptography analysis
            demonstrateSecurityProviders();
            SecurityAnalyzer.analyzeCryptographicCapabilities();
            SecurityAnalyzer.analyzeProviderSpecificFeatures();
            SecurityAnalyzer.testUnlimitedStrengthCrypto();
            
            // JVM monitoring and management
            JVMMonitor.analyzeJVMPerformance();
            JVMMonitor.analyzeMemoryAndGC();
            JVMMonitor.checkHotSpotSpecificBeans();
            JVMMonitor.analyzeThreadManagement();
            JVMMonitor.checkOracleSpecificFeatures();
            
            // Performance demonstrations
            demonstrateMemoryManagement();
            demonstrateJFRRecording();
            demonstratePerformanceMonitoring();
            
            System.out.println("=== Migration Demo Complete ===");
            System.out.println("This demo shows various Oracle JDK features that should be");
            System.out.println("verified when migrating to Eclipse Temurin JDK.");
            
        } catch (Exception e) {
            System.err.println("Demo error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Demonstrates JVM-specific information that may differ between vendors
     */
    private static void demonstrateJVMInfo() {
        System.out.println("1. JVM Information Analysis:");
        
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        System.out.println("   JVM Name: " + runtimeBean.getVmName());
        System.out.println("   JVM Vendor: " + runtimeBean.getVmVendor());
        System.out.println("   JVM Version: " + runtimeBean.getVmVersion());
        System.out.println("   VM Arguments: " + runtimeBean.getInputArguments());
        
        // Oracle-specific system properties that might not exist in other JVMs
        String[] oracleSpecificProps = {
            "sun.arch.data.model",
            "sun.boot.class.path",
            "sun.java.command",
            "sun.jnu.encoding",
            "sun.management.compiler"
        };
        
        System.out.println("   Oracle-specific properties:");
        for (String prop : oracleSpecificProps) {
            String value = System.getProperty(prop);
            if (value != null) {
                System.out.println("     " + prop + " = " + value);
            } else {
                System.out.println("     " + prop + " = [NOT AVAILABLE]");
            }
        }
        System.out.println();
    }
    
    /**
     * Demonstrates security provider configuration differences
     */
    private static void demonstrateSecurityProviders() {
        System.out.println("2. Security Providers Analysis:");
        
        // List all security providers
        java.security.Provider[] providers = Security.getProviders();
        System.out.println("   Available Security Providers:");
        for (java.security.Provider provider : providers) {
            System.out.println("     - " + provider.getName() + " v" + provider.getVersion() + 
                             " (" + provider.getInfo() + ")");
        }
        
        // Check for Oracle-specific providers
        String[] oracleProviders = {"SunPKCS11", "SunMSCAPI", "OracleUcrypto"};
        System.out.println("   Oracle-specific providers status:");
        for (String providerName : oracleProviders) {
            java.security.Provider provider = Security.getProvider(providerName);
            if (provider != null) {
                System.out.println("     ✓ " + providerName + " is available");
            } else {
                System.out.println("     ✗ " + providerName + " is NOT available");
            }
        }
        System.out.println();
    }
    
    /**
     * Demonstrates memory management and GC information
     */
    private static void demonstrateMemoryManagement() {
        System.out.println("3. Memory Management Analysis:");
        
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        System.out.println("   Heap Memory Usage: " + memoryBean.getHeapMemoryUsage());
        System.out.println("   Non-Heap Memory Usage: " + memoryBean.getNonHeapMemoryUsage());
        
        // Garbage Collector information
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        System.out.println("   Garbage Collectors:");
        for (GarbageCollectorMXBean gcBean : gcBeans) {
            System.out.println("     - " + gcBean.getName() + 
                             " (Collections: " + gcBean.getCollectionCount() + 
                             ", Time: " + gcBean.getCollectionTime() + "ms)");
        }
        
        // Force a small garbage collection for demonstration
        System.out.println("   Triggering GC for demonstration...");
        System.gc();
        System.out.println("   Post-GC Heap Usage: " + memoryBean.getHeapMemoryUsage());
        System.out.println();
    }
    
    /**
     * Demonstrates Java Flight Recorder functionality
     */
    private static void demonstrateJFRRecording() {
        System.out.println("4. Java Flight Recorder Demo:");
        
        try {
            Path recordingPath = Paths.get("migration_demo_recording.jfr");
            
            // Get the default configuration
            Configuration config = Configuration.getConfiguration("default");
            
            // Create and start a recording
            Recording recording = new Recording(config);
            recording.setDuration(Duration.ofSeconds(3));
            recording.setName("MigrationDemoRecording");
            System.out.println("   Starting JFR recording...");
            recording.start();
            
            // Do some computational work
            System.out.println("   Performing computational work...");
            doComputationalWork();
            
            // Stop the recording and dump to file
            recording.stop();
            recording.dump(recordingPath);
            System.out.println("   JFR recording saved to: " + recordingPath.toAbsolutePath());
            
            // Analyze the recording
            analyzeJFRRecording(recordingPath);
            
        } catch (Exception e) {
            System.err.println("   JFR Error: " + e.getMessage());
            System.err.println("   Note: JFR might not be available in all JVM distributions");
        }
        System.out.println();
    }
    
    /**
     * Demonstrates performance monitoring using MBeans
     */
    private static void demonstratePerformanceMonitoring() {
        System.out.println("5. Performance Monitoring via MBeans:");
        
        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            
            // Get compilation information
            ObjectName compilationName = new ObjectName("java.lang:type=Compilation");
            if (server.isRegistered(compilationName)) {
                String compilerName = (String) server.getAttribute(compilationName, "Name");
                Long totalCompilationTime = (Long) server.getAttribute(compilationName, "TotalCompilationTime");
                System.out.println("   Compiler: " + compilerName);
                System.out.println("   Total Compilation Time: " + totalCompilationTime + "ms");
            }
            
            // Check for HotSpot-specific MBeans
            ObjectName[] hotspotBeans = {
                new ObjectName("sun.management:type=HotspotClassLoading"),
                new ObjectName("sun.management:type=HotspotCompilation"),
                new ObjectName("sun.management:type=HotspotMemory"),
                new ObjectName("sun.management:type=HotspotRuntime"),
                new ObjectName("sun.management:type=HotspotThread")
            };
            
            System.out.println("   HotSpot-specific MBeans availability:");
            for (ObjectName beanName : hotspotBeans) {
                boolean available = server.isRegistered(beanName);
                System.out.println("     " + (available ? "✓" : "✗") + " " + beanName.getKeyProperty("type"));
            }
            
        } catch (Exception e) {
            System.err.println("   Performance monitoring error: " + e.getMessage());
        }
        System.out.println();
    }

           
    /**
     * Performs computational work for demonstration purposes
     */
    private static void doComputationalWork() {
        // Simulate various types of work that would show up in profiling
        
        // CPU-intensive work
        long sum = 0;
        for (long i = 0; i < 5_000_000; i++) {
            sum += Math.sqrt(i);
        }
        System.out.println("   Computation result: " + sum);
        
        // Memory allocation work
        java.util.List<String> strings = new java.util.ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            strings.add("String number " + i);
        }
        System.out.println("   Created " + strings.size() + " strings");
        
        // I/O simulation
        try {
            Thread.sleep(100); // Simulate I/O wait
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Analyzes a JFR recording file
     */
    private static void analyzeJFRRecording(Path path) {
        try {
            System.out.println("   Analyzing JFR recording...");
            List<RecordedEvent> events = RecordingFile.readAllEvents(path);
            
            // Print some basic info about the events captured
            System.out.println("   Total events recorded: " + events.size());
            
            if (!events.isEmpty()) {
                // Group events by name and count
                var eventCounts = events.stream()
                        .collect(Collectors.groupingBy(
                            event -> event.getEventType().getName(), 
                            Collectors.counting()
                        ));
                
                System.out.println("   Top event types recorded:");
                eventCounts.entrySet().stream()
                    .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                    .limit(5)
                    .forEach(entry -> 
                        System.out.println("     - " + entry.getKey() + ": " + entry.getValue()));
            }
                
        } catch (Exception e) {
            System.err.println("   Error reading JFR recording: " + e.getMessage());
        }
    }
}
