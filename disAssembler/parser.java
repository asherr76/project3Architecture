
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class parser {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Error: Proporciona el archivo .hack");
            System.exit(1);
        }

        String inFile = args[0];
        int dotIndex = inFile.lastIndexOf(".");
        String outFile = (dotIndex == -1) ? inFile + ".asm" : inFile.substring(0, dotIndex) + ".asm";

        symbolTable st = new symbolTable();
        cInstructions cInst = new cInstructions();
        List<String> rawLines = new ArrayList<>();
        List<Integer> lineNumbers = new ArrayList<>();
        List<String> errors = new ArrayList<>(); // Lista para acumular todos los errores

        try (BufferedReader br = new BufferedReader(new FileReader(inFile))) {
            String line;
            int currentLine = 0;
            while ((line = br.readLine()) != null) {
                currentLine++;
                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                        if (line.length() != 16) {
                            throw new IllegalArgumentException("La línea no tiene 16 bits.");
                        }
                        for (int i = 0; i < 16; i++) {
                            char c = line.charAt(i);
                            if (c != '0' && c != '1') {
                                throw new IllegalArgumentException("Carácter inválido (solo se admiten 0 y 1).");
                            }
                        }
                        rawLines.add(line);
                        lineNumbers.add(currentLine);
                    } catch (Exception e) {
                        errors.add("Error de formato en la línea " + currentLine + ": " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error de lectura: " + e.getMessage());
            System.exit(1);
        }

        if (!errors.isEmpty()) {
            System.err.println("\n-errores-");
            for (String err : errors) {
                System.err.println(err);
            }
            System.exit(1);
        }

        // primera pasada
        Set<Integer> jumpTargets = new TreeSet<>();
        for (int i = 0; i < rawLines.size(); i++) {
            String line = rawLines.get(i);
            if (line.startsWith("111") && !line.substring(13, 16).equals("000")) {
                if (i > 0 && rawLines.get(i - 1).startsWith("0")) {
                    int target = Integer.parseInt(rawLines.get(i - 1).substring(1), 2);
                    jumpTargets.add(target);
                }
            }
        }

        int labelCounter = 0;
        for (int target : jumpTargets) {
            st.addLabel(target, "L" + labelCounter);
            labelCounter++;
        }

        List<String> outputLines = new ArrayList<>();
        for (int i = 0; i < rawLines.size(); i++) {
            String line = rawLines.get(i);
            int originalLineNum = lineNumbers.get(i);

            try {
                if (st.containsAddress(i)) {
                    outputLines.add("(" + st.getLabel(i) + ")");
                }

                if (line.startsWith("0")) {
                    outputLines.add(aInstructions.parseA(line, st));
                } else if (line.startsWith("111")) {
                    String comp = line.substring(3, 10);
                    String dest = line.substring(10, 13);
                    String jump = line.substring(13, 16);
                    outputLines.add(cInst.parseC(dest, comp, jump));
                } else {
                    throw new IllegalArgumentException("Formato no soportado (los bits de inicio deben ser 0 o 111).");
                }
            } catch (Exception e) {
                errors.add("Error de desensamblado en la línea " + originalLineNum + ": " + e.getMessage());
            }
        }

        if (!errors.isEmpty()) {
            System.err.println("\n-errores-");
            for (String err : errors) {
                System.err.println(err);
            }
            System.exit(1);
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))) {
            for (String outLine : outputLines) {
                bw.write(outLine);
                bw.newLine();
            }
            System.out.println("Desensamblado completado con reconstrucción de etiquetas: " + outFile);
        } catch (Exception e) {
            System.err.println("Error al escribir el archivo: " + e.getMessage());
            System.exit(1);
        }
    }
}
