package com.example.app;

/**
 * Test class to verify that ColorProfileManager fails properly on Temurin JDK
 * when attempting to access Oracle-specific KCMS classes.
 */
public class ColorProfileManagerTest {
    
    /**
     * Tests that ColorProfileManager fails with ClassNotFoundException when running on non-Oracle JDK.
     * This test verifies that the code naturally fails when trying to access Oracle KCMS classes.
     */
    public static void testFailureOnTemurinJDK() {
        System.out.println("=== Testing ColorProfileManager failure on Temurin JDK ===");
        
        // Check current JVM to see if we're on Temurin
        String jvmVendor = System.getProperty("java.vendor", "").toLowerCase();
        String jvmName = System.getProperty("java.vm.name", "").toLowerCase();
        
        boolean isTemurinJDK = jvmVendor.contains("eclipse") || jvmVendor.contains("adoptium") || 
                              jvmVendor.contains("temurin") || jvmName.contains("openjdk");
        
        System.out.println("Current JVM: " + System.getProperty("java.vendor") + " " + System.getProperty("java.vm.name"));
        System.out.println("Is Temurin/Eclipse JDK: " + isTemurinJDK);
        
        if (isTemurinJDK) {
            // We expect this to fail with ClassNotFoundException
            try {
                ColorProfileManager.demonstrateColorOperations();
                
                // If we reach here, the test failed because no exception was thrown
                System.err.println("❌ TEST FAILED: Expected ClassNotFoundException but code succeeded!");
                System.err.println("ColorProfileManager should have failed on Temurin JDK");
                System.exit(1);
                
            } catch (RuntimeException e) {
                // Check if the root cause is ClassNotFoundException
                Throwable cause = e.getCause();
                if (cause instanceof ClassNotFoundException) {
                    ClassNotFoundException cnfe = (ClassNotFoundException) cause;
                    if (cnfe.getMessage().contains("sun.java2d.cmm.kcms.CMM")) {
                        System.out.println("✅ TEST PASSED: ColorProfileManager correctly failed with ClassNotFoundException");
                        System.out.println("   Expected failure occurred: " + cnfe.getMessage());
                        System.out.println("   This demonstrates that the code naturally fails when Oracle KCMS classes are not available");
                    } else {
                        System.err.println("❌ TEST FAILED: ClassNotFoundException occurred but for wrong class: " + cnfe.getMessage());
                        System.exit(1);
                    }
                } else {
                    System.err.println("❌ TEST FAILED: Expected ClassNotFoundException but got: " + 
                                     (cause != null ? cause.getClass().getName() : "null"));
                    e.printStackTrace();
                    System.exit(1);
                }
            } catch (Exception e) {
                System.err.println("❌ TEST FAILED: Unexpected exception type: " + e.getClass().getName());
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            System.out.println("⚠️  SKIPPING: This test is designed to run on Temurin JDK");
            System.out.println("   On Oracle JDK, the ColorProfileManager should work without exceptions");
        }
        
        System.out.println("=== Test completed successfully ===");
    }
    
    public static void main(String[] args) {
        testFailureOnTemurinJDK();
    }
}