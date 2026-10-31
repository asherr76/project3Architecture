#!/bin/bash
if [ -z "$1" ]; then
    echo "Error: Debes proporcionar el archivo .asm"
    echo "Uso: ./ensamblador.sh archivo.asm"
    exit 1
fi
java -cp bin Parser "$1"