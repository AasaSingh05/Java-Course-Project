#!/bin/bash
set -e

# === CONFIGURATION ===
JAVA_FX_PATH="$HOME/javafx/javafx-sdk-22/lib"
JDBC_JAR="libs/sqlite-jdbc-3.45.3.0.jar"

# === COMPILE ===
echo "Compiling project..."
javac -d out -cp "$JDBC_JAR:$JAVA_FX_PATH/*" src/**/*.java

# === RUN ===
echo "Launching Railway Reservation System..."
java --module-path "$JAVA_FX_PATH" \
     --add-modules javafx.controls,javafx.fxml \
     -cp "out:$JDBC_JAR" Main
