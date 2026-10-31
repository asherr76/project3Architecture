
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class parser {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Error: Proporciona el archivo .asm");
            System.exit(1);
        }

        String inFile = args[0];
        int dotIndex = inFile.lastIndexOf(".");
        String outFile = (dotIndex == -1) ? inFile + ".hack" : inFile.substring(0, dotIndex) + ".hack";

        symbolTable st = new symbolTable();
        List<String> rawLines = new ArrayList<>();
        List<Integer> lineNumbers = new ArrayList<>();
        List<String> errors = new ArrayList<>(); // Lista para acumular todos los errores

        try (BufferedReader br = new BufferedReader(new FileReader(inFile))) {
            String line;
            int currentLine = 0;
            while ((line = br.readLine()) != null) {
                currentLine++;
                int commentIdx = line.indexOf("//");
                if (commentIdx != -1) {
                    line = line.substring(0, commentIdx);
                }
                line = line.trim();
                if (!line.isEmpty()) {
                    rawLines.add(line);
                    lineNumbers.add(currentLine);
                }
            }
        } catch (Exception e) {
            System.err.println("Error de lectura: " + e.getMessage());
            System.exit(1);
        }

        // Primera pasada
        int romAddr = 0;
        for (int i = 0; i < rawLines.size(); i++) {
            String line = rawLines.get(i);
            int originalLineNum = lineNumbers.get(i);
            try {
                if (line.startsWith("(")) {
                    if (!line.endsWith(")")) {
                        throw new IllegalArgumentException("Falta ')' en la etiqueta.");
                    }
                    st.addEntry(line.substring(1, line.length() - 1).trim(), romAddr);
                } else {
                    romAddr++;
                }
            } catch (Exception e) {
                errors.add("Error de sintaxis en la línea " + originalLineNum + ": " + e.getMessage());
            }
        }

        if (!errors.isEmpty()) {
            System.err.println("\n-errores-");
            for (String err : errors) {
                System.err.println(err);
            }
            System.exit(1);
        }

        // Segunda pasada
        cInstructions cInst = new cInstructions();
        List<String> outputLines = new ArrayList<>(); // Guardamos el output en memoria

        for (int i = 0; i < rawLines.size(); i++) {
            String line = rawLines.get(i);
            int originalLineNum = lineNumbers.get(i);

            try {
                if (line.startsWith("(")) {
                    continue;
                }

                if (line.startsWith("@")) {
                    String sym = line.substring(1).trim();
                    if (sym.isEmpty()) {
                        throw new IllegalArgumentException("Instrucción A inválida (falta el valor o símbolo después del @).");
                    }

                    boolean isNumeric = true;
                    for (int j = 0; j < sym.length(); j++) {
                        if (!Character.isDigit(sym.charAt(j))) {
                            isNumeric = false;
                            break;
                        }
                    }
                    int address = isNumeric ? Integer.parseInt(sym) : st.getAddress(sym);
                    outputLines.add(aInstructions.translate(String.valueOf(address)));
                } else {
                    String dest = null, comp = line, jump = null;
                    int eqIdx = line.indexOf("=");
                    if (eqIdx != -1) {
                        dest = line.substring(0, eqIdx).trim();
                        comp = line.substring(eqIdx + 1).trim();
                    }
                    int semiIdx = comp.indexOf(";");
                    if (semiIdx != -1) {
                        jump = comp.substring(semiIdx + 1).trim();
                        comp = comp.substring(0, semiIdx).trim();
                    }

                    StringBuilder sb = new StringBuilder("111");
                    sb.append(cInst.comp(comp))
                            .append(cInst.dest(dest))
                            .append(cInst.jump(jump));
                    outputLines.add(sb.toString());
                }
            } catch (Exception e) {
                errors.add("Error de ensamblado en la línea " + originalLineNum + ": " + e.getMessage());
            }
        }

        if (!errors.isEmpty()) {
            System.err.println("\n--- ERRORES ENCONTRADOS ---");
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
            System.out.println("Ensamblado completado exitosamente: " + outFile);
        } catch (Exception e) {
            System.err.println("Error al escribir el archivo: " + e.getMessage());
            System.exit(1);
        }
    }
}
