package com.example.app;

import java.lang.management.*;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import java.util.List;

/**
 * Utility class for JVM monitoring and management.
 * Demonstrates features that might behave differently between Oracle JDK and Eclipse Temurin.
 */
public class JVMMonitor {
    
    /**
     * Analyzes JVM performance characteristics
     */
    public static void analyzeJVMPerformance() {
        System.out.println("--- JVM Performance Analysis ---");
        
        // Runtime information
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        System.out.println("JVM uptime: " + runtime.getUptime() + "ms");
        System.out.println("JVM start time: " + new java.util.Date(runtime.getStartTime()));
        
        // Compilation information
        CompilationMXBean compilation = ManagementFactory.getCompilationMXBean();
        if (compilation != null) {
            System.out.println("JIT Compiler: " + compilation.getName());
            if (compilation.isCompilationTimeMonitoringSupported()) {
                System.out.println("Total compilation time: " + compilation.getTotalCompilationTime() + "ms");
            }
        }
        
        // Class loading information
        ClassLoadingMXBean classLoading = ManagementFactory.getClassLoadingMXBean();
        System.out.println("Loaded classes: " + classLoading.getLoadedClassCount());
        System.out.println("Total loaded classes: " + classLoading.getTotalLoadedClassCount());
        System.out.println("Unloaded classes: " + classLoading.getUnloadedClassCount());
        
        System.out.println();
    }
    
    /**
     * Analyzes memory management and garbage collection
     */
    public static void analyzeMemoryAndGC() {
        System.out.println("--- Memory and Garbage Collection Analysis ---");
        
        // Memory usage
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memory.getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = memory.getNonHeapMemoryUsage();
        
        System.out.println("Heap Memory:");
        printMemoryUsage(heapUsage);
        System.out.println("Non-Heap Memory:");
        printMemoryUsage(nonHeapUsage);
        
        // Memory pools
        List<MemoryPoolMXBean> memoryPools = ManagementFactory.getMemoryPoolMXBeans();
        System.out.println("Memory Pools:");
        for (MemoryPoolMXBean pool : memoryPools) {
            System.out.println("  " + pool.getName() + " (" + pool.getType() + "):");
            printMemoryUsage(pool.getUsage());
        }
        
        // Garbage collectors
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        System.out.println("Garbage Collectors:");
        for (GarbageCollectorMXBean gc : gcBeans) {
            System.out.println("  " + gc.getName() + ":");
            System.out.println("    Collections: " + gc.getCollectionCount());
            System.out.println("    Collection time: " + gc.getCollectionTime() + "ms");
            System.out.println("    Memory pools: " + java.util.Arrays.toString(gc.getMemoryPoolNames()));
        }
        
        System.out.println();
    }
    
    /**
     * Checks for HotSpot-specific management beans
     */
    public static void checkHotSpotSpecificBeans() {
        System.out.println("--- HotSpot-Specific Management Beans ---");
        
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        
        // List of HotSpot-specific MBean names to check
        String[] hotspotMBeans = {
            "sun.management:type=HotspotClassLoading",
            "sun.management:type=HotspotCompilation", 
            "sun.management:type=HotspotMemory",
            "sun.management:type=HotspotRuntime",
            "sun.management:type=HotspotThread"
        };
        
        for (String mbeanName : hotspotMBeans) {
            try {
                ObjectName objName = new ObjectName(mbeanName);
                if (server.isRegistered(objName)) {
                    System.out.println("✓ " + mbeanName + " is available");
                    
                    // Try to get some basic info from the MBean
                    try {
                        Object info = server.getMBeanInfo(objName);
                        System.out.println("  MBean info retrieved successfully");
                    } catch (Exception e) {
                        System.out.println("  Could not retrieve MBean info: " + e.getMessage());
                    }
                } else {
                    System.out.println("✗ " + mbeanName + " is NOT available");
                }
            } catch (Exception e) {
                System.out.println("✗ Error checking " + mbeanName + ": " + e.getMessage());
            }
        }
        
        System.out.println();
    }
    
    /**
     * Analyzes thread management
     */
    public static void analyzeThreadManagement() {
        System.out.println("--- Thread Management Analysis ---");
        
        ThreadMXBean threads = ManagementFactory.getThreadMXBean();
        
        System.out.println("Current thread count: " + threads.getThreadCount());
        System.out.println("Peak thread count: " + threads.getPeakThreadCount());
        System.out.println("Total started threads: " + threads.getTotalStartedThreadCount());
        System.out.println("Daemon thread count: " + threads.getDaemonThreadCount());
        
        // CPU time monitoring (might not be available on all JVMs)
        if (threads.isCurrentThreadCpuTimeSupported()) {
            System.out.println("Current thread CPU time: " + threads.getCurrentThreadCpuTime() + "ns");
            System.out.println("CPU time monitoring supported: " + threads.isThreadCpuTimeEnabled());
        } else {
            System.out.println("Thread CPU time monitoring not supported");
        }
        
        // Contention monitoring
        if (threads.isThreadContentionMonitoringSupported()) {
            System.out.println("Thread contention monitoring supported: " + threads.isThreadContentionMonitoringEnabled());
        } else {
            System.out.println("Thread contention monitoring not supported");
        }
        
        System.out.println();
    }
    
    /**
     * Tests for Oracle-specific system properties and features
     */
    public static void checkOracleSpecificFeatures() {
        System.out.println("--- Oracle-Specific Features Check ---");
        
        // Oracle/Sun-specific system properties
        String[] oracleProps = {
            "sun.arch.data.model",
            "sun.boot.class.path", 
            "sun.cpu.endian",
            "sun.cpu.isalist",
            "sun.io.unicode.encoding",
            "sun.java.command",
            "sun.java.launcher",
            "sun.jnu.encoding",
            "sun.management.compiler",
            "sun.os.patch.level"
        };
        
        System.out.println("Oracle/Sun system properties:");
        for (String prop : oracleProps) {
            String value = System.getProperty(prop);
            if (value != null) {
                System.out.println("  ✓ " + prop + " = " + 
                    (value.length() > 50 ? value.substring(0, 50) + "..." : value));
            } else {
                System.out.println("  ✗ " + prop + " = [NOT AVAILABLE]");
            }
        }
        
        // Check for Oracle JVM-specific environment
        String javaVendor = System.getProperty("java.vendor", "").toLowerCase();
        String jvmName = System.getProperty("java.vm.name", "").toLowerCase();
        
        System.out.println("JVM Vendor Assessment:");
        if (javaVendor.contains("oracle")) {
            System.out.println("  ✓ Oracle JDK detected");
        } else if (javaVendor.contains("eclipse")) {
            System.out.println("  ✓ Eclipse Temurin detected");
        } else if (javaVendor.contains("openjdk")) {
            System.out.println("  ✓ OpenJDK detected");
        } else {
            System.out.println("  ? Unknown JDK vendor: " + javaVendor);
        }
        
        if (jvmName.contains("hotspot")) {
            System.out.println("  ✓ HotSpot JVM detected");
        } else if (jvmName.contains("openj9")) {
            System.out.println("  ✓ OpenJ9 JVM detected");
        } else {
            System.out.println("  ? Unknown JVM: " + jvmName);
        }
        
        System.out.println();
    }
    
    private static void printMemoryUsage(MemoryUsage usage) {
        if (usage != null) {
            System.out.println("    Used: " + formatBytes(usage.getUsed()));
            System.out.println("    Committed: " + formatBytes(usage.getCommitted()));
            System.out.println("    Max: " + (usage.getMax() == -1 ? "unlimited" : formatBytes(usage.getMax())));
        }
    }
    
    private static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
}
