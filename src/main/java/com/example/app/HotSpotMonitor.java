package com.example.app;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * Production-ready HotSpot Performance Monitor that uses Oracle HotSpot-specific MBeans.
 * This class demonstrates Oracle JDK incompatibility with Eclipse Temurin JDK by accessing
 * HotSpot-specific management beans that are only available in Oracle's HotSpot JVM.
 * 
 * Expected behavior:
 * - Oracle JDK 21: Successfully accesses HotSpot MBeans and provides detailed performance data
 * - Temurin JDK 21: Throws runtime exceptions due to missing HotSpot-specific MBeans
 */
public class HotSpotMonitor {
    
    private static final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
    
    // HotSpot-specific MBean object names
    private static final String HOTSPOT_COMPILATION_MBEAN = "sun.management:type=HotspotCompilation";
    private static final String HOTSPOT_MEMORY_MBEAN = "sun.management:type=HotspotMemory";
    private static final String HOTSPOT_RUNTIME_MBEAN = "sun.management:type=HotspotRuntime";
    private static final String HOTSPOT_THREAD_MBEAN = "sun.management:type=HotspotThread";
    private static final String HOTSPOT_CLASSLOADING_MBEAN = "sun.management:type=HotspotClassLoading";
    
    /**
     * Main demonstration method that showcases HotSpot-specific monitoring capabilities.
     * This method will work on Oracle JDK but fail on Temurin JDK.
     */
    public static void demonstrateHotSpotMonitoring() {
        System.out.println("6. HotSpot Performance Monitor (Oracle-specific):");
        
        try {
            // Verify we're running on a compatible JVM
            verifyHotSpotAvailability();
            
            // Get detailed HotSpot performance statistics
            getHotSpotCompilationStats();
            getHotSpotMemoryStats();
            getHotSpotRuntimeStats();
            
            System.out.println("   ✅ HotSpot monitoring completed successfully");
            
        } catch (Exception e) {
            System.err.println("   ❌ HotSpot monitoring failed: " + e.getMessage());
            System.err.println("   This indicates you're running on a non-Oracle JDK (e.g., Temurin)");
            System.err.println("   Oracle-specific HotSpot MBeans are not available in this JVM");
        }
        System.out.println();
    }
    
    /**
     * Verifies that HotSpot-specific MBeans are available in the current JVM.
     * Throws an exception if running on non-Oracle JDK.
     */
    private static void verifyHotSpotAvailability() throws Exception {
        String[] requiredMBeans = {
            HOTSPOT_COMPILATION_MBEAN,
            HOTSPOT_MEMORY_MBEAN,
            HOTSPOT_RUNTIME_MBEAN
        };
        
        for (String mbeanName : requiredMBeans) {
            ObjectName objName = new ObjectName(mbeanName);
            if (!mBeanServer.isRegistered(objName)) {
                throw new RuntimeException("Required HotSpot MBean not available: " + mbeanName);
            }
        }
        
        System.out.println("   HotSpot MBeans verified and available");
    }
    
    /**
     * Retrieves detailed JIT compiler statistics from HotSpot-specific MBeans.
     * Provides information about compilation threads, compile queues, and compiler performance.
     */
    public static void getHotSpotCompilationStats() throws Exception {
        ObjectName compilationMBean = new ObjectName(HOTSPOT_COMPILATION_MBEAN);
        
        System.out.println("   HotSpot JIT Compilation Statistics:");
        
        // Get compilation thread count
        Object compilerThreadCount = mBeanServer.getAttribute(compilationMBean, "CompilerThreadCount");
        System.out.println("     Compiler Threads: " + compilerThreadCount);
        
        // Get total compile time
        Object totalCompileTime = mBeanServer.getAttribute(compilationMBean, "TotalCompileTime");
        System.out.println("     Total Compile Time: " + totalCompileTime + "ms");
        
        // Get number of compiled methods
        Object compiledMethodCount = mBeanServer.getAttribute(compilationMBean, "TotalCompiles");
        System.out.println("     Total Compiled Methods: " + compiledMethodCount);
        
        // Get compiler statistics
        Object compilerStatistics = mBeanServer.getAttribute(compilationMBean, "MethodCompilationStatistic");
        if (compilerStatistics != null) {
            System.out.println("     Compilation Statistics Available: Yes");
        }
    }
    
    /**
     * Retrieves detailed memory management statistics from HotSpot-specific MBeans.
     * Provides information about internal memory pools and HotSpot-specific memory metrics.
     */
    public static void getHotSpotMemoryStats() throws Exception {
        ObjectName memoryMBean = new ObjectName(HOTSPOT_MEMORY_MBEAN);
        
        System.out.println("   HotSpot Memory Management Statistics:");
        
        // Get internal memory pool information
        Object internalMemoryCounters = mBeanServer.getAttribute(memoryMBean, "InternalMemoryCounters");
        if (internalMemoryCounters != null) {
            System.out.println("     Internal Memory Counters: Available");
        }
        
        // Get detailed heap information
        try {
            Object detailedHeapInfo = mBeanServer.getAttribute(memoryMBean, "VerboseGC");
            System.out.println("     Verbose GC Information: " + detailedHeapInfo);
        } catch (Exception e) {
            System.out.println("     Verbose GC Information: Not available");
        }
    }
    
    /**
     * Retrieves HotSpot runtime performance counters and VM-specific metrics.
     * Accesses low-level HotSpot performance data not available in standard JMX.
     */
    public static void getHotSpotRuntimeStats() throws Exception {
        ObjectName runtimeMBean = new ObjectName(HOTSPOT_RUNTIME_MBEAN);
        
        System.out.println("   HotSpot Runtime Performance Counters:");
        
        // Get HotSpot-specific runtime counters
        Object safePointCount = mBeanServer.getAttribute(runtimeMBean, "SafepointCount");
        System.out.println("     Safepoint Count: " + safePointCount);
        
        Object safePointTime = mBeanServer.getAttribute(runtimeMBean, "TotalSafepointTime");
        System.out.println("     Total Safepoint Time: " + safePointTime + "ms");
        
        // Try to get additional HotSpot-specific metrics
        try {
            Object vmOperations = mBeanServer.getAttribute(runtimeMBean, "VirtualMachineOperationNames");
            if (vmOperations != null) {
                System.out.println("     VM Operations: Available");
            }
        } catch (Exception e) {
            System.out.println("     VM Operations: Limited access");
        }
    }
}