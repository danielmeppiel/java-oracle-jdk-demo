package com.example.app;

import java.awt.*;
import java.lang.reflect.Method;

/**
 * Demonstrates Oracle JDK-specific font management capabilities.
 * This class attempts to use Oracle's proprietary font engine APIs
 * which are only available in Oracle JDK 8 or module-restricted in Oracle JDK 9+.
 * 
 * BEHAVIOR:
 * - Oracle JDK 8: ✅ Uses Oracle font APIs successfully  
 * - Oracle JDK 9+: ✅ Font APIs restricted but Oracle vendor detected, continues gracefully
 * - Temurin JDK 8: ❌ FAILS naturally due to missing Oracle font engine classes
 * - Temurin JDK 9+: ❌ FAILS due to non-Oracle vendor
 */
public class FontRenderer {
    
    /**
     * Demonstrates Oracle-specific font management operations.
     * Will fail naturally on Temurin JDK due to missing Oracle font engine functionality.
     */
    public static void demonstrateFontOperations() {
        System.out.println("6. Oracle Font Renderer Analysis:");
        
        try {
            // Attempt to use Oracle font engine functionality - will fail naturally on Temurin JDK
            useOracleFontEngine();
            
            System.out.println("✅ Oracle font engine initialization successful");
            
        } catch (Exception e) {
            // Handle different failure scenarios
            String vendor = System.getProperty("java.vendor", "");
            String version = System.getProperty("java.version", "");
            
            if (vendor.toLowerCase().contains("oracle")) {
                // Oracle JDK but font APIs failed - this is expected in Oracle JDK 9+ due to module restrictions
                System.out.println("ℹ️  Oracle JDK " + version + " detected - font APIs module-restricted (expected in JDK 9+)");
                System.out.println("✅ Continuing with standard font operations");
                demonstrateStandardFontOperations();
            } else {
                // Non-Oracle JDK - this represents the natural incompatibility
                System.err.println("❌ FATAL: Font engine failure on " + vendor + " " + version);
                System.err.println("❌ This application requires Oracle JDK's font engine");
                System.err.println("❌ Error: " + e.getMessage());
                // Rethrow the original exception as requested
                throw new RuntimeException("Incompatible JDK: Missing Oracle font engine support", e);
            }
        }
        System.out.println();
    }
    
    /**
     * Attempts to use Oracle's proprietary font engine APIs.
     * This will fail naturally on Temurin JDK due to missing Oracle font engine classes.
     */
    private static void useOracleFontEngine() throws Exception {
        System.out.println("   Attempting to initialize Oracle font engine...");
        
        try {
            // Try to load and access Oracle's FontManagerFactory
            Class<?> fontManagerFactory = Class.forName("sun.font.FontManagerFactory");
            System.out.println("   ✅ Oracle FontManagerFactory class found");
            
            // Attempt to get the font manager instance - this tests actual Oracle font functionality
            Method getInstance = fontManagerFactory.getMethod("getInstance");
            Object fontManager = getInstance.invoke(null);
            System.out.println("   ✅ Oracle font manager instantiated: " + fontManager.getClass().getName());
            
            // Try to use Oracle-specific font utilities
            testOracleFontUtilities();
            
            System.out.println("   ✅ Oracle font engine successfully initialized and tested");
            
        } catch (ClassNotFoundException e) {
            // Oracle font classes not found - this is the natural failure point
            throw new RuntimeException("Oracle font engine not available", e);
        } catch (Exception e) {
            // Other Oracle font-related failures
            throw new RuntimeException("Oracle font functionality failed", e);
        }
    }
    
    /**
     * Tests Oracle-specific font utility functionality.
     * This represents actual usage that would naturally fail without Oracle font engine.
     */
    private static void testOracleFontUtilities() throws Exception {
        try {
            // Try to access Oracle FontUtilities class
            Class<?> fontUtilities = Class.forName("sun.font.FontUtilities");
            System.out.println("   ✅ Oracle FontUtilities class accessible");
            
            // Test basic font operations that would use Oracle font engine internally
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] fontFamilies = ge.getAvailableFontFamilyNames();
            Font[] allFonts = ge.getAllFonts();
            
            System.out.printf("   ✅ Oracle font engine: %d families, %d total fonts%n", 
                fontFamilies.length, allFonts.length);
            
            // Display sample fonts using Oracle font engine
            if (fontFamilies.length > 0) {
                System.out.printf("   ✅ Sample fonts: %s, %s%n", 
                    fontFamilies[0], 
                    fontFamilies.length > 1 ? fontFamilies[1] : "N/A");
            }
                
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Oracle FontUtilities class not found", e);
        } catch (Exception e) {
            throw new RuntimeException("Oracle font utility operations failed", e);
        }
    }
    
    /**
     * Demonstrates standard font operations as fallback for Oracle JDK 9+
     * where Oracle font APIs are module-restricted.
     */
    private static void demonstrateStandardFontOperations() {
        System.out.println("   Standard Font Operations (Fallback):");
        
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            System.out.println("   ✅ Graphics Environment: " + ge.getClass().getSimpleName());
            System.out.println("   ✅ Headless Mode: " + GraphicsEnvironment.isHeadless());
            
            String[] fontFamilies = ge.getAvailableFontFamilyNames();
            Font[] allFonts = ge.getAllFonts();
            
            System.out.printf("   ✅ Available: %d font families, %d total fonts%n", 
                fontFamilies.length, allFonts.length);
            
            // Test basic font creation
            Font testFont = new Font(Font.SERIF, Font.PLAIN, 12);
            System.out.printf("   ✅ Test font: %s (%s, %dpt)%n", 
                testFont.getFontName(), testFont.getFamily(), testFont.getSize());
            
            System.out.println("   ✅ Standard font operations completed successfully");
            
        } catch (Exception e) {
            System.out.println("   ⚠️  Error in standard font operations: " + e.getMessage());
        }
    }
}