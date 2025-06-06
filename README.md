# Oracle JDK to Eclipse Temurin Migration Demo

This project demonstrates a comprehensive Java application that showcases various Oracle JDK features and how they behave during migration to Eclipse Temurin JDK.

## Overview

The demo application includes functionality that tests:

1. **Java Flight Recorder (JFR)** - Performance monitoring and profiling
2. **Security Providers** - Cryptographic capabilities and security configurations
3. **JVM Management** - Memory management, garbage collection, and performance monitoring
4. **HotSpot-specific Features** - Oracle JVM-specific management beans and properties
5. **System Properties** - Oracle/Sun-specific system properties
6. **Performance Monitoring** - MBean-based monitoring and analysis

## Migration Scenarios Demonstrated

### 1. Java Flight Recorder (JFR)
- **Oracle JDK**: Full JFR support with commercial features
- **Eclipse Temurin**: JFR available in OpenJDK 11+ (verify feature parity)
- **Testing**: Run JFR recordings and verify event availability

### 2. Security Providers
- **Oracle JDK**: Includes Oracle-specific security providers
- **Eclipse Temurin**: May have different default providers
- **Testing**: Verify cryptographic algorithm availability and security provider compatibility

### 3. JVM Management and Monitoring
- **Oracle JDK**: HotSpot-specific MBeans and management features
- **Eclipse Temurin**: Same HotSpot JVM, but verify MBean availability
- **Testing**: Check management bean registration and functionality

### 4. System Properties and JVM Options
- **Oracle JDK**: Oracle/Sun-specific system properties
- **Eclipse Temurin**: May not have all Oracle-specific properties
- **Testing**: Verify application behavior when properties are missing

### 5. Performance Characteristics
- **Oracle JDK**: Specific GC tuning and performance optimizations
- **Eclipse Temurin**: Verify similar performance with same JVM options
- **Testing**: Compare performance metrics and GC behavior

## Project Structure

```
src/main/java/com/example/app/
├── HelloWorldApp.java      # Main demo application
├── SecurityAnalyzer.java   # Security and cryptography analysis
└── JVMMonitor.java        # JVM monitoring and management utilities

Configuration Files:
├── jvm-options.txt        # JVM options for migration testing
├── app.policy           # Security policy file
└── run-demo.sh          # Demo runner script
```

## Running the Demo

### Prerequisites
- Java 11+ (Oracle JDK or Eclipse Temurin)
- Maven 3.6+

### Basic Run
```bash
mvn clean package
java -jar target/java-modern-test-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### Using the Demo Script
```bash
./run-demo.sh
```

### With JFR Enabled
```bash
java -XX:+FlightRecorder \
     -XX:StartFlightRecording=duration=60s,filename=migration-test.jfr \
     -jar target/java-modern-test-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### With Custom JVM Options
```bash
java @jvm-options.txt \
     -jar target/java-modern-test-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Migration Testing Checklist

When migrating from Oracle JDK to Eclipse Temurin, verify:

- [ ] **Application Startup**: No missing class or module errors
- [ ] **JFR Functionality**: Flight recordings work as expected
- [ ] **Security Operations**: All cryptographic operations succeed
- [ ] **Performance**: Similar performance characteristics
- [ ] **Monitoring**: Management beans and monitoring tools work
- [ ] **System Properties**: Application handles missing Oracle-specific properties
- [ ] **JVM Options**: All JVM options are supported or have alternatives
- [ ] **Third-party Dependencies**: All libraries are compatible

## Key Differences to Watch For

### 1. Security Providers
- Oracle JDK may include additional security providers
- Verify your application's cryptographic requirements

### 2. System Properties
- Oracle-specific properties (sun.*, oracle.*) may not be available
- Ensure your application gracefully handles missing properties

### 3. JVM Options
- Some Oracle-specific JVM flags may not be recognized
- Test with Eclipse Temurin to identify unsupported options

### 4. Performance Tuning
- GC behavior should be similar (both use HotSpot)
- Verify performance-critical code paths

### 5. Management and Monitoring
- Most JMX beans should be identical
- Verify monitoring tools and dashboards work correctly

## Output Analysis

The demo application produces detailed output showing:

1. **JVM Information**: Vendor, version, and runtime details
2. **Security Analysis**: Available providers and algorithms
3. **Memory Management**: Heap usage, GC statistics, memory pools
4. **Performance Metrics**: Compilation times, thread statistics
5. **JFR Analysis**: Flight recorder event summaries

Look for differences in output when running on Oracle JDK vs Eclipse Temurin.

## Troubleshooting

### Common Issues During Migration

1. **Missing Security Providers**: Some Oracle-specific providers may not be available
2. **JVM Option Warnings**: Unknown JVM flags will generate warnings
3. **System Property Dependencies**: Code relying on Oracle-specific properties may fail
4. **Performance Differences**: Minor variations in performance are normal

### Solutions

1. **Security**: Use standard security providers or add Bouncy Castle
2. **JVM Options**: Remove or replace Oracle-specific flags
3. **Properties**: Add defensive code to handle missing properties
4. **Performance**: Re-tune JVM options if significant differences occur

## Further Reading

- [Eclipse Temurin Migration Guide](https://adoptium.net/migration/)
- [OpenJDK Security Providers](https://openjdk.java.net/groups/security/)
- [Java Flight Recorder Documentation](https://docs.oracle.com/javacomponents/jmc-5-4/jfr-runtime-guide/)
- [JVM Options Reference](https://chriswhocodes.com/hotspot_options_jdk11.html)
