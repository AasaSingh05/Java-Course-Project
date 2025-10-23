#!/bin/bash
set -euo pipefail

# -------- Config --------
JAVA_FX_PATH="/home/bigmannova/javafx/javafx-sdk-22.0.2/lib"
JDBC_JAR="libs/sqlite-jdbc-3.45.3.0.jar"

SRC_DIR="src"
OUT_DIR="out"

# If Main.java has a package, set FQCN to that (e.g., com.example.Main).
# If there is NO package in Main.java, set FQCN="Main".
FQCN="Main"   # or "your.package.Main"
# ------------------------

echo "Cleaning output..."
rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

echo "Collecting sources..."
# Create a sources file robustly (handles nested dirs without relying on ** glob).
find "$SRC_DIR" -type f -name "*.java" | sort > sources.txt

if [[ ! -s sources.txt ]]; then
  echo "No Java sources found under $SRC_DIR"
  exit 1
fi

echo "Compiling project..."
javac \
  -d "$OUT_DIR" \
  --module-path "$JAVA_FX_PATH" \
  --add-modules javafx.controls,javafx.fxml \
  -cp "$JDBC_JAR" \
  @sources.txt

# Optional: sanity check that Main.class exists in expected location
# This helps catch wrong FQCN values early.
MAIN_PATH="${FQCN//.//}.class"
if [[ ! -f "$OUT_DIR/$MAIN_PATH" ]]; then
  echo "Warning: $OUT_DIR/$MAIN_PATH not found. Check FQCN='$FQCN' and package declaration in Main.java."
fi

echo "Launching Railway Reservation System..."
java \
  --module-path "$JAVA_FX_PATH" \
  --add-modules javafx.controls,javafx.fxml \
  -cp "$OUT_DIR:$JDBC_JAR" \
  "$FQCN"
