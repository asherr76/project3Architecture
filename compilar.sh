#!/bin/bash
echo "Compilando archivos..."
mkdir -p bin
javac -d bin *.java
echo "Compilación finalizada."