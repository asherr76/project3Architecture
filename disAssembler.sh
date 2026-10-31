#!/bin/bash
if [ -z "$1" ]; then
    echo "Error: Debes proporcionar el archivo .hack"
    echo "Uso: ./desensamblador.sh archivo.hack"
    exit 1
fi
java -cp bin Parser "$1"