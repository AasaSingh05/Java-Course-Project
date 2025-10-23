#!/bin/bash

javac -d out -cp "libs/sqlite-jdbc-3.45.3.0.jar:/usr/share/openjfx/lib/*" src/**/*.java

java --module-path /usr/share/openjfx/lib \
     --add-modules javafx.controls,javafx.fxml \
     -cp "out:libs/sqlite-jdbc-3.45.3.0.jar" Main
