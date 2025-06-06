# Oracle JDK Incompatibility Demonstration

This project demonstrates Oracle JDK proprietary features that **will cause applications to fail** when migrating to Eclipse Temurin JDK. The application is designed to:

- ✅ **Run successfully on Oracle JDK** 
- ❌ **Fail with errors on Eclipse Temurin JDK**

## What This Demonstrates

This application uses Oracle proprietary components that are **not available** in Eclipse Temurin:

| Component | Oracle JDK 8 | Oracle JDK 11+ | Temurin JDK 8 | Temurin 11+ |
|-----------|--------------|-----------------|---------------|-------------|
| **KCMS Color Management** | ✅ Available | ❌ Removed | ❌ Missing | ❌ Missing |
| **Oracle Font Engine** | ✅ Available | 🔒 Restricted | ❌ Missing | ❌ Missing |
| **sun.misc.Unsafe** | ✅ Available | ⚠️ Deprecated | ⚠️ May work | ⚠️ May work |
| **HotSpot MBeans** | ✅ Available | ✅ Available | ❌ Missing | ❌ Missing |
| **Oracle Security Providers** | ✅ Available | ✅ Available | ❌ Missing | ❌ Missing |

> **Note**: Some incompatibilities like KCMS are **only visible in JDK 8**, as Oracle removed these features in JDK 9+.

## Expected Test Results

### Oracle JDK (any version)
```
✅ All components initialize successfully
✅ Application completes without errors
```

### Eclipse Temurin JDK
```
❌ FATAL: Color management failure (JDK 8 only)
❌ FATAL: Font engine failure  
❌ ERROR: HotSpot monitoring failed
❌ Application exits with RuntimeException
```

## Quick Test

### Prerequisites
- Java 8 or 11+ (Oracle JDK **and** Eclipse Temurin for comparison)
- Maven 3.6+

### Run the Test
```bash
# Build the project
mvn clean package

# Test with your current JDK
java -jar target/java-modern-test-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### Compare Results
1. **With Oracle JDK**: Should see all ✅ success messages
2. **With Temurin JDK**: Should see ❌ failure messages and RuntimeExceptions

## Incompatibility Details

Each class demonstrates a specific Oracle proprietary feature:

- **`ColorProfileManager`** - Uses KCMS (Kodac Color Matching System) - **JDK 8 only**
- **`FontRenderer`** - Uses Oracle T2K font engine
- **`MemoryOptimizer`** - Uses `sun.misc.Unsafe` internal API  
- **`HotSpotMonitor`** - Accesses Oracle HotSpot-specific MBeans
- **`SecurityAnalyzer`** - Tests Oracle-specific security providers

## Migration Guidance

For real applications experiencing these issues:

1. **Replace KCMS** → Use LCMS (Little Color Matching System)
2. **Replace T2K fonts** → Use FreeType font rendering
3. **Replace sun.misc.Unsafe** → Use `ByteBuffer.allocateDirect()`
4. **Replace HotSpot MBeans** → Use standard JMX beans
5. **Replace Oracle security** → Use Bouncy Castle or standard providers

**📖 Full migration guide**: [Eclipse Temurin Migration Documentation](https://adoptium.net/docs/migration/)
