package com.example.app;

import sun.font.FontManagerFactory;
import sun.font.FontUtilities;
import java.awt.*;

/**
 * FontRenderer class that demonstrates Oracle JDK-specific font APIs.
 * Uses internal Oracle font APIs (sun.font.*) that are not available in Eclipse Temurin JDK.
 * Will compile on Oracle JDK 21 but fail on Temurin JDK 21.
 */
public class FontRenderer {
    
    /**
     * Main demonstration method that showcases Oracle font operations
     */
    public static void demonstrateFontOperations() {
        System.out.println("6. Oracle Font Renderer Analysis:");
        
        try {
            analyzeFontManager();
            getFontMetrics();
            analyzeFontCapabilities();
        } catch (Exception e) {
            System.err.println("   Font Renderer Error: " + e.getMessage());
            System.err.println("   Note: Oracle font APIs may not be available in this JVM distribution");
        }
        System.out.println();
    }
    
    /**
     * Analyzes the Oracle font manager using proprietary APIs
     */
    private static void analyzeFontManager() {
        System.out.println("   Oracle Font Manager Analysis:");
        
        // Access Oracle's FontManagerFactory - fails on Temurin
        Object fontManager = FontManagerFactory.getInstance();
        System.out.println("     Font Manager Class: " + fontManager.getClass().getName());
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontFamilies = ge.getAvailableFontFamilyNames();
        
        System.out.println("     Available Font Families: " + fontFamilies.length);
        System.out.println("     Total Font Variants: " + ge.getAllFonts().length);
        
        // Display sample font families
        System.out.println("     Sample Font Families:");
        for (int i = 0; i < Math.min(5, fontFamilies.length); i++) {
            System.out.println("       - " + fontFamilies[i]);
        }
    }
    
    /**
     * Gets detailed font metrics using Oracle font utilities
     */
    private static void getFontMetrics() {
        System.out.println("   Font Metrics Analysis:");
        
        Font[] testFonts = {
            new Font(Font.SERIF, Font.PLAIN, 12),
            new Font(Font.SANS_SERIF, Font.BOLD, 14),
            new Font(Font.MONOSPACED, Font.ITALIC, 16)
        };
        
        for (Font font : testFonts) {
            System.out.println("     Font: " + font.getFontName() + " (" + font.getSize() + "pt)");
            System.out.println("       Family: " + font.getFamily());
            System.out.println("       Style: " + getFontStyleName(font.getStyle()));
            System.out.println("       PostScript Name: " + font.getPSName());
        }
    }
    
    /**
     * Analyzes Oracle-specific font engine capabilities
     */
    private static void analyzeFontCapabilities() {
        System.out.println("   Oracle Font Engine Capabilities:");
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        System.out.println("     Headless Mode: " + GraphicsEnvironment.isHeadless());
        System.out.println("     Graphics Environment: " + ge.getClass().getSimpleName());
        
        GraphicsDevice[] devices = ge.getScreenDevices();
        System.out.println("     Graphics Devices: " + devices.length);
        
        if (devices.length > 0) {
            System.out.println("     Default Device: " + ge.getDefaultScreenDevice().getIDstring());
        }
    }
    
    /**
     * Helper method to get font style name
     */
    private static String getFontStyleName(int style) {
        switch (style) {
            case Font.PLAIN: return "Plain";
            case Font.BOLD: return "Bold";
            case Font.ITALIC: return "Italic";
            case Font.BOLD | Font.ITALIC: return "Bold Italic";
            default: return "Unknown (" + style + ")";
        }
    }
}