# INFORME DE CÓDIGO
## PROYECTO 3
Cada sub proyecto consta de 4 archivos:
```
└── subproyecto/ 
    ├── aInstuctions.java -> procesa las instrucciones tipo A
    ├── cInstructions.java -> procesa las instrucciones tipo C
    ├── symbolTable.java -> se encarga de la tabla de símbolos de procesa las etiquetas
    ├── parser.java -> archivo principal que lee el archivo de entrada, unifica el parseo y genera el archivo traducido
```    
## ENSAMBLADOR
Es el que cumple con el archivo 6 de Nand2Tetris
### aInstructions.java
Recibe el string, descarta el primer cero y luego devuelve el numero en decimal con Integer.parseInt(num, baseX), en este caso base 2. Verifica con SymbolTable si no es una etiqueta o una variable reservada, y si no solo pone @ y el numero que representa.
### cInstructions.java
Desglosa los comandos en tres. Cada método recibe el pedazo y lo busca en sus condicionales internos para devolver el patrón binario exacto que enciende los circuitos físicos de la CPU. Al final, estos tres pedazos se juntan en el `Parser`, se les pega el prefijo `111` al inicio) y forman la cadena final de 16 bits.
### symbolTable.java
Usa un Hashmap para asociar nombres y posiciones y utiliza addEntry y getAddress para gestionarlo. Cuando el `Parser` descubre una etiqueta de salto en su primera lectura, `addEntry` la anota en la libreta indicando a qué línea exacta del código debe saltar. En la segunda lectura, si el programa pide crear una variable nueva, `getAddress` le asigna automáticamente la siguiente "casa" o espacio libre en la memoria RAM (comenzando siempre desde la dirección 16) y le devuelve ese número numérico al `Parser` para que pueda traducirlo.
### parser.java
Utiliza main junto con `BufferedReader` y `BufferedWriter` para interactuar con los archivos. Analiza el archivo en dos pasadas. En la primera pasada, busca etiquetas como `(LOOP)` para anotar en qué línea se encuentran. En la segunda pasada, traduce el texto final: si ve un `@`, le pasa el trabajo al traductor de direcciones; si ve una operación (con `=` o `;`), llama al intérprete de operaciones, y finalmente anota la línea binaria terminada en el archivo `.hack`.

## DESENSAMBLADOR
Hace lo contrario al proyecto 6
### aInstructions.java
Recibe un número en String (ya sea un número puro como `"10"` o el valor que devolvió el `HashMap`). Luego usa Integer.parseInt() para convertir ese texto en número y luego calcula su valor binario. Como el IDE solo lee bloques, utiliza herramientas de manipulación de texto (como `StringBuilder`) dentro de un bucle para rellenar el resultado con ceros a la izquierda. Asi la cadena mide exactamente 16 caracteres y que el bit más significativo es siempre un `0`.
### cInstructions.java
Recibe la instruccion ya partida, y usa usa especie de diccionario HashMap para juntar el binario con su igual en comando y devolverlo. Luego se juntan los pedazos y, si el comando incluye destiny o jump, pone ';' y '='.
### symbolTable.java
Utiliza un HashMap<Integer, String>. En el ensamblador la clave era el texto, pero aquí es al revés: la clave es el número de la dirección de memoria (Integer) y el valor es el texto reconstruido (String). Si el `Parser` pide buscar una variable y el `HashMap` responde que no existe, esta clase la añade y le asigna un contador interno que empieza en 16 (el primer espacio libre de la RAM), sumando 1 cada vez que entra una variable nueva.
### parser.java
Utiliza un `BufferedReader` dentro de un bucle para cargar las líneas en un `ArrayList<String>` para contar los caracteres: si una línea no tiene exactamente 16 bits o tiene algo distinto a `0` y `1`, lanza una excepción. Hace dos pasadas a las lineas con for, primero recorre buscando jumps y si lo encuentra mira la instrucción anterior para saber a qué línea se dirige. Esas direcciones destino las guarda en un `TreeSet<Integer>`. Se utiliza un `TreeSet` en lugar de una lista normal porque el tree ya ordena los números de menor a mayor y elimina duplicados. En la segunda pasada, por línea, verifica si ese número de línea está guardado como una etiqueta. Luego, si el texto empieza con 0 usa los métodos de aInstructions; si empieza con `111`, lo corta con el método `substring()` en tres pedazos exactos y lo procesa con los metodos de cInstructions.

## GESTIÓN DE ERRORES
Se hace mediante:
* **Rastreo de líneas originales:** Utilizan un `ArrayList<Integer>` llamado `lineNumbers`. Como durante la lectura se eliminan los comentarios y las líneas vacías, las posiciones de las instrucciones cambian. Esta lista actúa como un ancla, recordando exactamente de qué línea física del archivo original vino cada instrucción válida.
* **Acumulación de fallos:** Emplean un `ArrayList<String>` llamado `errors`. Todo el código de traducción está envuelto en bloques de seguridad `try-catch`. Si una operación falla, en lugar de dejar que Java arroje un error feo del sistema, el programa atrapa la excepción (`Exception`), redacta un mensaje amigable indicando la línea del problema y lo guarda en esta lista para continuar leyendo.
* **Cierre seguro:** Antes de abrir el `BufferedWriter` para crear el archivo final (`.hack` o `.asm`), el programa revisa la lista `errors`. Si la lista tiene al menos un elemento, imprime el reporte completo en la terminal y ejecuta el comando `System.exit(1)` para abortar. El archivo final de salida **solo se crea en el disco duro si el código original es 100% perfecto**.

### Validaciones Específicas del Ensamblador (.asm a .hack)
El ensamblador busca errores de sintaxis y de lógica humana:
* **Etiquetas mal formadas:** Durante la primera pasada, verifica que toda etiqueta que empiece con `(` termine correctamente con `)`.
* **Instrucciones A vacías:** Al procesar variables, verifica que el símbolo `@` esté acompañado de un número o texto. Si encuentra un `@` solitario, lanza una excepción de sintaxis inmediatamente.
* **Comandos inexistentes (Instrucciones C):** Cuando el `Parser` le entrega los fragmentos a la clase `CInstructions`, esta revisa si la operación matemática o el salto existen en el hardware real. Si escribimos algo que la máquina no entiende (como `FALSO=D`), el diccionario interno lanza un `IllegalArgumentException` que se anota en el reporte.

### Validaciones Específicas del Desensamblador (.hack a .asm)
El desensamblador busca errores estructurales y archivos binarios corruptos:
* **Filtro Estricto de Formato:** Su control de calidad es el más severo y comienza desde la misma lectura del archivo. Utiliza un bucle `for` para escanear cada carácter uno por uno. Si una línea no tiene **exactamente 16 bits**, o si contiene caracteres ajenos al `0` y `1` (como letras u otros números), el programa detecta corrupción estructural y aborta la lectura para protegerse.
* **Detección de Operaciones Falsas:** Durante la segunda pasada, si el programa lee un binario que empieza por `111` pero los bits de cómputo (los del medio) no corresponden a ninguna configuración electrónica real de la CPU (ALU), el traductor rechaza el paquete y lanza un error de "Formato no soportado", indicando que ese código máquina es inválido.
