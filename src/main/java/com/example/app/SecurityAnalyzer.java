package com.example.app;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.Provider;
import java.util.Arrays;
import java.util.Set;

/**
 * Utility class to analyze security configurations and cryptographic capabilities.
 * Useful for identifying differences between Oracle JDK and Eclipse Temurin JDK
 * in terms of available security providers and algorithms.
 */
public class SecurityAnalyzer {
    
    /**
     * Analyzes and reports on cryptographic capabilities
     */
    public static void analyzeCryptographicCapabilities() {
        System.out.println("--- Cryptographic Capabilities Analysis ---");
        
        // Test key algorithms that might differ between JDK distributions
        testAlgorithmAvailability("AES", "KeyGenerator");
        testAlgorithmAvailability("RSA", "KeyPairGenerator");
        testAlgorithmAvailability("EC", "KeyPairGenerator");
        testAlgorithmAvailability("HmacSHA256", "Mac");
        
        // Test cipher availability
        testCipherAvailability("AES/GCM/NoPadding");
        testCipherAvailability("ChaCha20-Poly1305");
        testCipherAvailability("AES/ECB/PKCS5Padding");
        
        System.out.println();
    }
    
    /**
     * Analyzes security provider-specific features
     */
    public static void analyzeProviderSpecificFeatures() {
        System.out.println("--- Provider-Specific Features Analysis ---");
        
        Provider[] providers = Security.getProviders();
        
        for (Provider provider : providers) {
            System.out.println("Provider: " + provider.getName() + " v" + provider.getVersion());
            
            // Check for specific services that might be Oracle-specific
            checkProviderService(provider, "KeyStore", "PKCS12");
            checkProviderService(provider, "TrustManagerFactory", "PKIX");
            checkProviderService(provider, "SecureRandom", "SHA1PRNG");
            checkProviderService(provider, "Signature", "SHA256withRSA");
            
            // Look for Oracle-specific algorithms
            Set<String> algorithms = provider.stringPropertyNames();
            long oracleSpecific = algorithms.stream()
                .filter(prop -> prop.contains("Oracle") || prop.contains("Sun"))
                .count();
            
            if (oracleSpecific > 0) {
                System.out.println("  Found " + oracleSpecific + " Oracle/Sun-specific properties");
            }
            
            System.out.println();
        }
    }
    
    /**
     * Tests unlimited strength cryptography availability
     */
    public static void testUnlimitedStrengthCrypto() {
        System.out.println("--- Unlimited Strength Cryptography Test ---");
        
        try {
            // Test if unlimited strength cryptography is available
            int maxKeyLength = Cipher.getMaxAllowedKeyLength("AES");
            System.out.println("Maximum allowed AES key length: " + maxKeyLength);
            
            if (maxKeyLength >= 256) {
                System.out.println("✓ Unlimited strength cryptography is available");
            } else {
                System.out.println("✗ Limited strength cryptography (old JCE policy files?)");
            }
            
            // Test with a 256-bit AES key
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey key = keyGen.generateKey();
            System.out.println("✓ Successfully generated 256-bit AES key");
            
        } catch (Exception e) {
            System.out.println("✗ Error testing unlimited strength crypto: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static void testAlgorithmAvailability(String algorithm, String service) {
        try {
            if ("KeyGenerator".equals(service)) {
                KeyGenerator.getInstance(algorithm);
                System.out.println("✓ " + algorithm + " (" + service + ") is available");
            } else if ("KeyPairGenerator".equals(service)) {
                java.security.KeyPairGenerator.getInstance(algorithm);
                System.out.println("✓ " + algorithm + " (" + service + ") is available");
            } else if ("Mac".equals(service)) {
                javax.crypto.Mac.getInstance(algorithm);
                System.out.println("✓ " + algorithm + " (" + service + ") is available");
            }
        } catch (NoSuchAlgorithmException e) {
            System.out.println("✗ " + algorithm + " (" + service + ") is NOT available");
        }
    }
    
    private static void testCipherAvailability(String transformation) {
        try {
            Cipher.getInstance(transformation);
            System.out.println("✓ Cipher " + transformation + " is available");
        } catch (Exception e) {
            System.out.println("✗ Cipher " + transformation + " is NOT available");
        }
    }
    
    private static void checkProviderService(Provider provider, String service, String algorithm) {
        String key = service + "." + algorithm;
        if (provider.getProperty(key) != null) {
            System.out.println("  ✓ Supports " + service + "/" + algorithm);
        }
    }
}
