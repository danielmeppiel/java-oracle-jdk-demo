package com.example.app;

import sun.misc.Unsafe; // This import will fail on Temurin JDK
import java.lang.reflect.Field;

/**
 * Memory optimizer using Oracle JDK's internal sun.misc.Unsafe API.
 * This class demonstrates incompatibility with Eclipse Temurin JDK.
 * 
 * WARNING: This code uses internal Oracle JDK APIs that are:
 * - Not available in all JDK distributions
 * - Not part of the public API contract
 * - May be removed in future JDK versions
 */
public class MemoryOptimizer {
    
    private static final Unsafe UNSAFE;
    private static final long ALLOCATED_MEMORY_SIZE = 1024 * 1024; // 1MB
    
    static {
        try {
            // Access Unsafe through reflection (standard approach)
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            UNSAFE = (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access sun.misc.Unsafe - " +
                "This feature requires Oracle JDK and is not available in Eclipse Temurin", e);
        }
    }
    
    /**
     * Demonstrates direct memory operations using Unsafe API.
     * This method will fail on Eclipse Temurin JDK due to missing sun.misc.Unsafe.
     */
    public static void demonstrateDirectMemoryOperations() {
        System.out.println("6. Direct Memory Operations via sun.misc.Unsafe:");
        System.out.println("   WARNING: This demonstrates Oracle JDK internal API usage");
        System.out.println("   NOTE: sun.misc.Unsafe is deprecated and may be removed in future JDK versions");
        
        try {
            System.out.println("   ✓ Successfully accessed sun.misc.Unsafe");
            System.out.println("   Unsafe instance: " + UNSAFE.getClass().getName());
            
            // Show JVM vendor to highlight compatibility concerns
            String vendor = System.getProperty("java.vendor");
            String vmName = System.getProperty("java.vm.name");
            System.out.println("   Current JVM: " + vendor + " (" + vmName + ")");
            
            if (vendor.contains("Eclipse") || vendor.contains("Adoptium")) {
                System.out.println("   ⚠️  WARNING: Running on Temurin JDK - sun.misc.Unsafe access succeeded but");
                System.out.println("   ⚠️  this API is deprecated and may be removed or restricted in future versions");
            }
            
            // Demonstrate direct memory allocation
            long memoryAddress = allocateDirectMemory(ALLOCATED_MEMORY_SIZE);
            System.out.println("   ✓ Allocated " + (ALLOCATED_MEMORY_SIZE / 1024) + "KB direct memory at address: 0x" + 
                             Long.toHexString(memoryAddress));
            
            // Demonstrate memory writing and reading
            demonstrateMemoryOperations(memoryAddress);
            
            // Demonstrate memory copying
            long copyAddress = demonstrateMemoryCopy(memoryAddress);
            
            // Clean up allocated memory
            freeDirectMemory(memoryAddress);
            freeDirectMemory(copyAddress);
            System.out.println("   ✓ Direct memory cleanup completed");
            
            System.out.println("   ⚠️  MIGRATION CONCERN: Code successfully used Oracle JDK internal APIs");
            System.out.println("   ⚠️  Consider using safer alternatives like ByteBuffer.allocateDirect()");
            
        } catch (Exception e) {
            System.err.println("   ❌ ERROR: Direct memory operations failed: " + e.getMessage());
            System.err.println("   This failure indicates Oracle JDK incompatibility with current JVM");
            System.err.println("   Recommendation: Replace sun.misc.Unsafe usage with standard Java APIs");
            throw new RuntimeException("Oracle JDK incompatibility detected", e);
        }
        System.out.println();
    }
    
    /**
     * Allocates direct memory outside the JVM heap using Unsafe.
     */
    private static long allocateDirectMemory(long size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Memory size must be positive");
        }
        
        long address = UNSAFE.allocateMemory(size);
        if (address == 0) {
            throw new OutOfMemoryError("Failed to allocate " + size + " bytes of direct memory");
        }
        
        // Initialize memory to zero for safety
        UNSAFE.setMemory(address, size, (byte) 0);
        return address;
    }
    
    /**
     * Frees previously allocated direct memory.
     */
    private static void freeDirectMemory(long address) {
        if (address != 0) {
            UNSAFE.freeMemory(address);
        }
    }
    
    /**
     * Demonstrates basic memory read/write operations.
     */
    private static void demonstrateMemoryOperations(long address) {
        // Write some test data
        UNSAFE.putLong(address, 0x123456789ABCDEFL);
        UNSAFE.putInt(address + 8, 0x12345678);
        UNSAFE.putByte(address + 12, (byte) 0xFF);
        
        // Read back the data
        long longValue = UNSAFE.getLong(address);
        int intValue = UNSAFE.getInt(address + 8);
        byte byteValue = UNSAFE.getByte(address + 12);
        
        System.out.println("   ✓ Memory write/read test successful:");
        System.out.println("     Long value: 0x" + Long.toHexString(longValue));
        System.out.println("     Int value: 0x" + Integer.toHexString(intValue));
        System.out.println("     Byte value: 0x" + Integer.toHexString(byteValue & 0xFF));
    }
    
    /**
     * Demonstrates memory copying using Unsafe.
     */
    private static long demonstrateMemoryCopy(long sourceAddress) {
        long destinationAddress = allocateDirectMemory(ALLOCATED_MEMORY_SIZE);
        
        // Copy memory from source to destination
        UNSAFE.copyMemory(sourceAddress, destinationAddress, ALLOCATED_MEMORY_SIZE);
        
        // Verify the copy by comparing some values
        long sourceValue = UNSAFE.getLong(sourceAddress);
        long destValue = UNSAFE.getLong(destinationAddress);
        
        if (sourceValue == destValue) {
            System.out.println("   ✓ Memory copy operation successful - " + 
                             (ALLOCATED_MEMORY_SIZE / 1024) + "KB copied");
        } else {
            throw new RuntimeException("Memory copy verification failed");
        }
        
        return destinationAddress;
    }
}