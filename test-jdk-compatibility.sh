#!/usr/bin/env bash

# Oracle JDK Incompatibility Demonstration Test Script
# This script tests the application with different JDK versions to demonstrate compatibility issues

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration - Set via environment variables or use defaults
# JDK Names and Paths (compatible with bash 3.2)
JDK_NAMES=("Oracle_8" "Temurin_8" "Oracle_21" "Temurin_21")
JDK_ORACLE_8="${ORACLE_JDK8_HOME:-/Library/Java/JavaVirtualMachines/jdk1.8.0_XXX.jdk/Contents/Home}"
JDK_TEMURIN_8="${TEMURIN_JDK8_HOME:-/Library/Java/JavaVirtualMachines/temurin-8.jdk/Contents/Home}"
JDK_ORACLE_21="${ORACLE_JDK21_HOME:-/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home}"
JDK_TEMURIN_21="${TEMURIN_JDK21_HOME:-/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home}"

# Function to get JDK path by name
get_jdk_path() {
    local jdk_name=$1
    case "$jdk_name" in
        "Oracle_8") echo "$JDK_ORACLE_8" ;;
        "Temurin_8") echo "$JDK_TEMURIN_8" ;;
        "Oracle_21") echo "$JDK_ORACLE_21" ;;
        "Temurin_21") echo "$JDK_TEMURIN_21" ;;
        *) echo "" ;;
    esac
}

# Default Maven path (can be overridden with MAVEN_HOME environment variable)
MAVEN_CMD="${MAVEN_HOME:+$MAVEN_HOME/bin/}mvn"

# Function to print colored output
print_header() {
    echo -e "\n${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}\n"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

# Function to check if JDK path exists
check_jdk() {
    local jdk_name=$1
    local jdk_path=$2
    
    if [[ -d "$jdk_path" ]]; then
        print_success "Found $jdk_name at: $jdk_path"
        return 0
    else
        print_warning "JDK not found: $jdk_name at $jdk_path"
        return 1
    fi
}

# Function to get JDK version info
get_jdk_info() {
    local jdk_path=$1
    "$jdk_path/bin/java" -version 2>&1 | head -n 3
}

# Function to compile with specific JDK
compile_with_jdk() {
    local jdk_name=$1
    local jdk_path=$2
    
    print_info "Compiling with $jdk_name..."
    
    # Set JAVA_HOME for Maven
    export JAVA_HOME="$jdk_path"
    
    # Clean and compile
    if [[ "$jdk_name" == *"8"* ]]; then
        # For JDK 8, adjust compiler settings
        $MAVEN_CMD clean compile -Dmaven.compiler.source=1.8 -Dmaven.compiler.target=1.8 -q
        $MAVEN_CMD package -Dmaven.compiler.source=1.8 -Dmaven.compiler.target=1.8 -q
    else
        # For JDK 11+
        $MAVEN_CMD clean package -q
    fi
    
    if [[ $? -eq 0 ]]; then
        print_success "Compilation successful with $jdk_name"
        return 0
    else
        print_error "Compilation failed with $jdk_name"
        return 1
    fi
}

# Function to run with specific JDK
run_with_jdk() {
    local jdk_name=$1
    local jdk_path=$2
    
    print_info "Running application with $jdk_name..."
    echo "----------------------------------------"
    
    # Set JAVA_HOME
    export JAVA_HOME="$jdk_path"
    
    # Find the JAR file
    local jar_file
    if [[ "$jdk_name" == *"8"* ]]; then
        jar_file="target/java-modern-test-1.0-SNAPSHOT-jar-with-dependencies.jar"
    else
        jar_file="target/java-modern-test-1.0-SNAPSHOT-jar-with-dependencies.jar"
    fi
    
    if [[ ! -f "$jar_file" ]]; then
        print_error "JAR file not found: $jar_file"
        return 1
    fi
    
    # Run the application and capture output
    local exit_code=0
    local output
    output=$("$jdk_path/bin/java" -jar "$jar_file" 2>&1) || exit_code=$?
    
    # Print the output
    echo "$output"
    
    echo "----------------------------------------"
    
    # Check for error indicators in output even if exit code is 0
    local has_fatal_error=false
    if echo "$output" | grep -q "FATAL:\|Exception\|Error:"; then
        has_fatal_error=true
    fi
    
    if [[ $exit_code -eq 0 && $has_fatal_error == false ]]; then
        print_success "Application completed successfully with $jdk_name"
        return 0
    else
        if [[ $has_fatal_error == true ]]; then
            print_error "Application failed with $jdk_name (FATAL error or exception detected)"
        else
            print_error "Application failed with $jdk_name (exit code: $exit_code)"
        fi
        return 1
    fi
}

# Function to show current configuration
show_config() {
    print_header "Current JDK Configuration"
    
    echo "Environment Variables:"
    echo "  ORACLE_JDK8_HOME  = ${ORACLE_JDK8_HOME:-[not set]}"
    echo "  TEMURIN_JDK8_HOME = ${TEMURIN_JDK8_HOME:-[not set]}"
    echo "  ORACLE_JDK21_HOME = ${ORACLE_JDK21_HOME:-[not set]}"
    echo "  TEMURIN_JDK21_HOME= ${TEMURIN_JDK21_HOME:-[not set]}"
    echo "  MAVEN_HOME        = ${MAVEN_HOME:-[not set]}"
    echo
    
    echo "Resolved Paths:"
    for jdk_name in "${JDK_NAMES[@]}"; do
        local jdk_path=$(get_jdk_path "$jdk_name")
        local status="❌ Not found"
        if [[ -d "$jdk_path" ]]; then
            status="✅ Found"
        fi
        printf "  %-12s = %s %s\n" "$jdk_name" "$jdk_path" "$status"
    done
    echo
}

# Function to test a specific JDK
test_jdk() {
    local jdk_name=$1
    local jdk_path=$2
    
    print_header "Testing $jdk_name"
    
    # Check if JDK exists
    if ! check_jdk "$jdk_name" "$jdk_path"; then
        print_warning "Skipping $jdk_name - not found"
        return 1
    fi
    
    # Show JDK info
    print_info "JDK Information:"
    get_jdk_info "$jdk_path"
    echo
    
    # Compile
    if ! compile_with_jdk "$jdk_name" "$jdk_path"; then
        print_error "Skipping execution due to compilation failure"
        return 1
    fi
    
    # Run
    run_with_jdk "$jdk_name" "$jdk_path"
    local run_result=$?
    
    # Interpretation
    echo
    if [[ "$jdk_name" == *"Oracle"* ]]; then
        if [[ $run_result -eq 0 ]]; then
            print_success "EXPECTED: Oracle JDK should work successfully"
        else
            print_error "UNEXPECTED: Oracle JDK should not fail"
        fi
    else
        if [[ $run_result -eq 0 ]]; then
            print_warning "UNEXPECTED: Temurin JDK should fail due to incompatibilities"
        else
            print_success "EXPECTED: Temurin JDK fails due to missing Oracle proprietary features"
        fi
    fi
    
    return $run_result
}

# Function to detect installed JDKs
detect_jdks() {
    print_header "Detecting Installed JDKs"
    
    local found_jdks=()
    
    # Common JDK installation paths on macOS
    local jdk_base="/Library/Java/JavaVirtualMachines"
    
    if [[ -d "$jdk_base" ]]; then
        for jdk_dir in "$jdk_base"/*.jdk; do
            if [[ -d "$jdk_dir" ]]; then
                local jdk_name=$(basename "$jdk_dir" .jdk)
                local jdk_path="$jdk_dir/Contents/Home"
                
                if [[ -f "$jdk_path/bin/java" ]]; then
                    echo -e "Found: ${GREEN}$jdk_name${NC} at $jdk_path"
                    
                    # Try to categorize
                    if [[ "$jdk_name" == *"oracle"* ]] || [[ "$jdk_name" == *"jdk1.8"* ]]; then
                        found_jdks+=("Oracle:$jdk_path")
                    elif [[ "$jdk_name" == *"temurin"* ]] || [[ "$jdk_name" == *"adoptopenjdk"* ]]; then
                        found_jdks+=("Temurin:$jdk_path")
                    else
                        found_jdks+=("Unknown:$jdk_path")
                    fi
                fi
            fi
        done
    fi
    
    if [[ ${#found_jdks[@]} -eq 0 ]]; then
        print_warning "No JDKs found in $jdk_base"
        print_info "Please update the JDK_PATHS array in this script with your JDK locations"
    fi
    
    echo
}

# Function to show usage
show_usage() {
    echo "Usage: $0 [OPTIONS] [JDK_NAME]"
    echo
    echo "Options:"
    echo "  -h, --help      Show this help message"
    echo "  -c, --config    Show current JDK configuration"
    echo "  -d, --detect    Detect installed JDKs"
    echo "  -a, --all       Test all configured JDKs"
    echo
    echo "JDK Names:"
    echo "  Oracle_8        Test Oracle JDK 8"
    echo "  Temurin_8       Test Eclipse Temurin JDK 8"
    echo "  Oracle_21       Test Oracle JDK 21"
    echo "  Temurin_21      Test Eclipse Temurin JDK 21"
    echo
    echo "Examples:"
    echo "  $0 --config                    # Show current configuration"
    echo "  $0 --detect                    # Detect installed JDKs"
    echo "  $0 --all                       # Test all JDKs"
    echo "  $0 Oracle_8                    # Test only Oracle JDK 8"
    echo "  $0 Temurin_21                  # Test only Temurin JDK 21"
    echo
    echo "Environment Variables:"
    echo "  ORACLE_JDK8_HOME     Path to Oracle JDK 8"
    echo "  TEMURIN_JDK8_HOME    Path to Eclipse Temurin JDK 8"
    echo "  ORACLE_JDK21_HOME    Path to Oracle JDK 21"
    echo "  TEMURIN_JDK21_HOME   Path to Eclipse Temurin JDK 21"
    echo "  MAVEN_HOME           Path to Maven installation (optional)"
    echo
    echo "Example with environment variables:"
    echo "  export ORACLE_JDK8_HOME=/path/to/oracle-jdk8"
    echo "  export TEMURIN_JDK21_HOME=/path/to/temurin-jdk21"
    echo "  $0 --all"
}

# Main function
main() {
    print_header "Oracle JDK Incompatibility Test Suite"
    
    # Parse command line arguments
    case "${1:-}" in
        -h|--help)
            show_usage
            exit 0
            ;;
        -c|--config)
            show_config
            exit 0
            ;;
        -d|--detect)
            detect_jdks
            exit 0
            ;;
        -a|--all)
            # Test all JDKs
            local overall_result=0
            for jdk_name in "${JDK_NAMES[@]}"; do
                local jdk_path=$(get_jdk_path "$jdk_name")
                test_jdk "$jdk_name" "$jdk_path" || overall_result=1
                echo
            done
            
            print_header "Test Summary"
            if [[ $overall_result -eq 0 ]]; then
                print_info "All tests completed (check individual results above)"
            else
                print_info "Some tests failed or showed expected incompatibilities"
            fi
            exit $overall_result
            ;;
        "")
            print_info "No JDK specified. Use --help for usage information."
            detect_jdks
            exit 1
            ;;
        *)
            # Test specific JDK
            local jdk_name="$1"
            local jdk_path=$(get_jdk_path "$jdk_name")
            if [[ -n "$jdk_path" ]]; then
                test_jdk "$jdk_name" "$jdk_path"
            else
                print_error "Unknown JDK: $jdk_name"
                echo "Available JDKs: ${JDK_NAMES[*]}"
                exit 1
            fi
            ;;
    esac
}

# Run main function with all arguments
main "$@"
