# PROYECTO 3 ORGANIZACIÓN DE COMPUTADORES
## PROFESOR : José Luis Montoya P.

Elabora: Ashly Sofia Robayo

## DESCRIPCIÓN
Proyecto en java con 2 subproyectos: se hace el proyecto 6 de nand2tetris.org y su contrario, un desensamblador. El proyecto 6 pasa de un archivo en hackAssembler a un archivo en binario .hack para el CPU Emulator. El desensamblador pasa de un archivo en binario a uno en HackAssembler.

## COMPILACIÓN Y EJECUCIÓN
Para ejecutar este proyecto, necesitas tener instalado Java y usar una terminal compatible con bash (como Git Bash en Windows, o la terminal por defecto en Linux/macOS).

Antes de correr los scripts, debes otorgarles permisos de ejecución abriendo la terminal en la carpeta del proyecto y ejecutando:
```bash
chmod +x *.sh
```
Luego, para compilar los archivos java y generar la carpeta **bin**

```bash
./compilar.sh
```

Para usar el ensamblador, llamarlo seguido del archivo a traducir
```bash
./assembler.sh PruebaX.asm
``` 

Y para el desensamblador, se hace lo mismo
```bash
./disAssembler.sh PruebaX.hack
```