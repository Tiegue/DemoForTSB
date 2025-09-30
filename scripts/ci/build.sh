#!/usr/bin/env bash
set -euo pipefail

echo "🔨 Building DemoFortsb Banking Application..."

# Environment setup (PermGen removed in Java 8+, so no MaxPermSize)
export MAVEN_OPTS="-Xmx2048m"

# Clean previous builds
echo "🧹 Cleaning previous builds..."
mvn clean -B -q

# Compile and validate
echo "⚙️ Compiling source code..."
mvn compile -B -q

# Package application
echo "📦 Packaging application..."
mvn package -DskipTests -Dspring.profiles.active=local -B -q

# Verify package(s) exist
shopt -s nullglob
artifacts=(target/*.jar)
if (( ${#artifacts[@]} == 0 )); then
  echo "❌ JAR file not found in target/"
  exit 1
fi

echo "✅ Build completed successfully!"
echo "📦 Artifact(s):"
for f in "${artifacts[@]}"; do
  # Cross-platform size print without GNU/BSD stat differences: use wc
  size_bytes=$(wc -c < "$f")
  echo " - $(basename "$f") (${size_bytes} bytes)"
done