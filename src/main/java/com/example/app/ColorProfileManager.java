package com.example.app;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.color.ICC_ColorSpace;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

/**
 * Demonstrates Oracle JDK color management capabilities using KCMS (Kodak Color Management System).
 * This class showcases incompatibilities between Oracle JDK and Eclipse Temurin JDK
 * in color profile management and color space transformations.
 * 
 * Oracle JDK uses KCMS internally, while Temurin JDK uses LCMS (Little Color Management System).
 * This creates potential compatibility issues for applications relying on Oracle-specific
 * color management behavior.
 */
public class ColorProfileManager {
    
    /**
     * Demonstrates Oracle-specific color management operations that may not work
     * identically on Eclipse Temurin JDK due to different Color Management Module (CMM) implementations.
     */
    public static void demonstrateColorOperations() {
        System.out.println("--- Color Profile Management Analysis ---");
        
        try {
            // Analyze current color management system
            analyzeColorManagementSystem();
            
            // Test Oracle KCMS-specific functionality
            testOracleKCMSFeatures();
            
            // Analyze ICC profile handling
            analyzeICCProfileHandling();
            
            // Test color space transformations
            testColorSpaceTransformations();
            
        } catch (Exception e) {
            System.err.println("Color management error: " + e.getMessage());
            System.err.println("This indicates potential incompatibility between Oracle JDK and Temurin JDK");
        }
        
        System.out.println();
    }
    
    /**
     * Analyzes the current color management system implementation
     */
    private static void analyzeColorManagementSystem() {
        System.out.println("Color Management System Analysis:");
        
        // Check CMM system properties
        String[] cmmProperties = {
            "sun.java2d.cmm",
            "java.awt.color.cmm",
            "sun.java2d.cmm.kcms.lib",
            "sun.java2d.cmm.lcms.lib"
        };
        
        for (String prop : cmmProperties) {
            String value = System.getProperty(prop);
            if (value != null) {
                System.out.println("  ✓ " + prop + " = " + value);
            } else {
                System.out.println("  ✗ " + prop + " = [NOT AVAILABLE]");
            }
        }
        
        // Determine which CMM is being used
        detectColorManagementModule();
    }
    
    /**
     * Detects whether Oracle KCMS or LCMS is being used
     */
    private static void detectColorManagementModule() {
        System.out.println("CMM Detection:");
        
        // Try to access Oracle KCMS classes
        try {
            Class.forName("sun.java2d.cmm.kcms.CMM");
            System.out.println("  ✓ Oracle KCMS CMM detected");
        } catch (ClassNotFoundException e) {
            System.out.println("  ✗ Oracle KCMS CMM not available");
        }
        
        try {
            Class.forName("sun.java2d.cmm.kcms.KcmsServiceProvider");
            System.out.println("  ✓ Oracle KCMS Service Provider detected");
        } catch (ClassNotFoundException e) {
            System.out.println("  ✗ Oracle KCMS Service Provider not available");
        }
        
        // Check for LCMS (used by Temurin)
        try {
            Class.forName("sun.java2d.cmm.lcms.LCMS");
            System.out.println("  ✓ LCMS (Little CMS) detected - indicates non-Oracle JDK");
        } catch (ClassNotFoundException e) {
            System.out.println("  ✗ LCMS not available");
        }
    }
    
    /**
     * Tests Oracle KCMS-specific features that may not be available in Temurin JDK
     */
    private static void testOracleKCMSFeatures() {
        System.out.println("Oracle KCMS Feature Testing:");
        
        try {
            // Attempt to access Oracle's KCMS CMM directly
            Class<?> kcmsCMM = Class.forName("sun.java2d.cmm.kcms.CMM");
            Constructor<?> constructor = kcmsCMM.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object cmmInstance = constructor.newInstance();
            
            System.out.println("  ✓ Oracle KCMS CMM instantiated successfully");
            System.out.println("  ✓ This indicates Oracle JDK with KCMS support");
            
            // Try to access KCMS-specific methods
            Method[] methods = kcmsCMM.getDeclaredMethods();
            long kcmsSpecificMethods = java.util.Arrays.stream(methods)
                .filter(m -> m.getName().contains("kcms") || m.getName().contains("transform"))
                .count();
            
            System.out.println("  ✓ Found " + kcmsSpecificMethods + " KCMS-specific methods");
            
        } catch (ClassNotFoundException e) {
            System.out.println("  ✗ Oracle KCMS CMM not found - likely running on non-Oracle JDK");
            System.out.println("  ✗ This is expected on Eclipse Temurin JDK");
        } catch (Exception e) {
            System.out.println("  ✗ Error accessing Oracle KCMS: " + e.getMessage());
        }
        
        // Test Oracle-specific color management properties
        testOracleColorProperties();
    }
    
    /**
     * Tests Oracle-specific color management system properties
     */
    private static void testOracleColorProperties() {
        System.out.println("Oracle Color Management Properties:");
        
        // Oracle KCMS-specific properties
        String[] oracleColorProps = {
            "sun.java2d.cmm.kcms.dir",
            "sun.java2d.cmm.kcms.profile.dir", 
            "sun.java2d.cmm.kcms.library",
            "oracle.java2d.cmm.kcms.enabled"
        };
        
        boolean hasOracleProps = false;
        for (String prop : oracleColorProps) {
            String value = System.getProperty(prop);
            if (value != null) {
                System.out.println("  ✓ " + prop + " = " + value);
                hasOracleProps = true;
            }
        }
        
        if (!hasOracleProps) {
            System.out.println("  ✗ No Oracle KCMS properties found");
            System.out.println("  ✗ This suggests non-Oracle JDK environment");
        }
    }
    
    /**
     * Analyzes ICC profile handling differences between Oracle and Temurin JDK
     */
    private static void analyzeICCProfileHandling() {
        System.out.println("ICC Profile Handling Analysis:");
        
        try {
            // Get standard sRGB profile
            ICC_Profile srgbProfile = ICC_Profile.getInstance(ColorSpace.CS_sRGB);
            System.out.println("  ✓ sRGB Profile Class: " + srgbProfile.getClass().getName());
            System.out.println("  ✓ Profile Class: " + srgbProfile.getProfileClass());
            System.out.println("  ✓ Color Space Type: " + srgbProfile.getColorSpaceType());
            
            // Try to access Oracle-specific profile methods using reflection
            checkOracleProfileMethods(srgbProfile);
            
            // Test profile data access
            byte[] profileData = srgbProfile.getData();
            System.out.println("  ✓ Profile data size: " + profileData.length + " bytes");
            
            // Check profile header information
            if (profileData.length >= 128) {
                String signature = new String(profileData, 36, 4);
                System.out.println("  ✓ Profile signature: " + signature);
            }
            
        } catch (Exception e) {
            System.out.println("  ✗ Error analyzing ICC profiles: " + e.getMessage());
        }
    }
    
    /**
     * Checks for Oracle-specific profile methods using reflection
     */
    private static void checkOracleProfileMethods(ICC_Profile profile) {
        System.out.println("Oracle-specific Profile Methods:");
        
        try {
            // Look for Oracle/Sun-specific methods
            Method[] methods = profile.getClass().getDeclaredMethods();
            
            boolean foundOracleMethods = false;
            for (Method method : methods) {
                String methodName = method.getName();
                if (methodName.contains("kcms") || methodName.contains("sun") || 
                    methodName.contains("oracle") || methodName.toLowerCase().contains("transform")) {
                    System.out.println("  ✓ Found Oracle method: " + methodName);
                    foundOracleMethods = true;
                }
            }
            
            if (!foundOracleMethods) {
                System.out.println("  ✗ No Oracle-specific profile methods found");
            }
            
        } catch (Exception e) {
            System.out.println("  ✗ Error checking Oracle profile methods: " + e.getMessage());
        }
    }
    
    /**
     * Tests color space transformations that may behave differently between CMM implementations
     */
    private static void testColorSpaceTransformations() {
        System.out.println("Color Space Transformation Testing:");
        
        try {
            // Create color spaces
            ColorSpace srgb = ColorSpace.getInstance(ColorSpace.CS_sRGB);
            ColorSpace ciexyz = ColorSpace.getInstance(ColorSpace.CS_CIEXYZ);
            
            System.out.println("  ✓ sRGB ColorSpace: " + srgb.getClass().getName());
            System.out.println("  ✓ CIEXYZ ColorSpace: " + ciexyz.getClass().getName());
            
            // Test color transformation
            float[] srgbColor = {1.0f, 0.5f, 0.0f}; // Orange in sRGB
            float[] xyzColor = srgb.toCIEXYZ(srgbColor);
            
            System.out.printf("  ✓ sRGB [%.3f, %.3f, %.3f] -> XYZ [%.3f, %.3f, %.3f]%n",
                srgbColor[0], srgbColor[1], srgbColor[2],
                xyzColor[0], xyzColor[1], xyzColor[2]);
            
            // Test reverse transformation
            float[] backToSrgb = srgb.fromCIEXYZ(xyzColor);
            System.out.printf("  ✓ XYZ [%.3f, %.3f, %.3f] -> sRGB [%.3f, %.3f, %.3f]%n",
                xyzColor[0], xyzColor[1], xyzColor[2],
                backToSrgb[0], backToSrgb[1], backToSrgb[2]);
            
            // Check transformation accuracy (should be very close)
            float deltaR = Math.abs(srgbColor[0] - backToSrgb[0]);
            float deltaG = Math.abs(srgbColor[1] - backToSrgb[1]);
            float deltaB = Math.abs(srgbColor[2] - backToSrgb[2]);
            
            if (deltaR < 0.001f && deltaG < 0.001f && deltaB < 0.001f) {
                System.out.println("  ✓ Color transformation accuracy: EXCELLENT");
            } else {
                System.out.printf("  ⚠ Color transformation accuracy: Δ[%.6f, %.6f, %.6f]%n",
                    deltaR, deltaG, deltaB);
                System.out.println("  ⚠ Different CMM implementations may show slight variations");
            }
            
        } catch (Exception e) {
            System.out.println("  ✗ Error in color transformations: " + e.getMessage());
        }
    }
}