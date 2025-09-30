#!/bin/bash
set -euo pipefail

TEST_TYPE="${1:-all}"

echo "🧪 Running tests: $TEST_TYPE"

case "$TEST_TYPE" in
  "--unit")
    echo "🔬 Running unit tests..."
    mvn test -Dtest=**/*Test -Dspring.profiles.active=local -B
    ;;
    
  "--integration")
    echo "🔗 Running integration tests..."
    mvn test -Dtest=**/*IT -Dspring.profiles.active=local -B
    ;;
    
  "--security")
    echo "🔒 Running security tests..."
    mvn test -Dtest=**/*SecurityTest -Dspring.profiles.active=local -B
    ;;
    
  *)
    echo "🧪 Running all tests..."
    mvn test  -Dspring.profiles.active=local -B
    ;;
esac

# Generate coverage comment out for now using Qodana
#mvn jacoco:report -B

echo "✅ Tests completed!"
