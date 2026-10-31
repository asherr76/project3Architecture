
import java.util.HashMap;
import java.util.Map;

public class cInstructions {

    private final Map<String, String> compMap = new HashMap<>();
    private final Map<String, String> destMap = new HashMap<>();
    private final Map<String, String> jumpMap = new HashMap<>();

    public cInstructions() {
        compMap.put("0101010", "0");
        compMap.put("0111111", "1");
        compMap.put("0111010", "-1");
        compMap.put("0001100", "D");
        compMap.put("0110000", "A");
        compMap.put("0001101", "!D");
        compMap.put("0110001", "!A");
        compMap.put("0000111", "-D");
        compMap.put("0110011", "-A");
        compMap.put("0011111", "D+1");
        compMap.put("0110111", "A+1");
        compMap.put("0001110", "D-1");
        compMap.put("0110010", "A-1");
        compMap.put("0000010", "D+A");
        compMap.put("0010011", "D-A");
        compMap.put("0000000", "D&A");
        compMap.put("0010101", "D|A");

        compMap.put("1110000", "M");
        compMap.put("1110001", "!M");
        compMap.put("1110011", "-M");
        compMap.put("1110111", "M+1");
        compMap.put("1110010", "M-1");
        compMap.put("1000010", "D+M");
        compMap.put("1010011", "D-M");
        compMap.put("1000111", "M-D");
        compMap.put("1000000", "D&M");
        compMap.put("1010101", "D|M");

        destMap.put("000", "");
        destMap.put("001", "M");
        destMap.put("010", "D");
        destMap.put("011", "MD");
        destMap.put("100", "A");
        destMap.put("101", "AM");
        destMap.put("110", "AD");
        destMap.put("111", "AMD");

        jumpMap.put("000", "");
        jumpMap.put("001", "JGT");
        jumpMap.put("010", "JEQ");
        jumpMap.put("011", "JGE");
        jumpMap.put("100", "JLT");
        jumpMap.put("101", "JNE");
        jumpMap.put("110", "JLE");
        jumpMap.put("111", "JMP");
    }

    public String parseC(String destBin, String compBin, String jumpBin) {
        if (!compMap.containsKey(compBin)) {
            throw new IllegalArgumentException("Binario COMP inválido: " + compBin);
        }
        if (!destMap.containsKey(destBin)) {
            throw new IllegalArgumentException("Binario DEST inválido: " + destBin);
        }
        if (!jumpMap.containsKey(jumpBin)) {
            throw new IllegalArgumentException("Binario JUMP inválido: " + jumpBin);
        }

        String dest = destMap.get(destBin);
        String comp = compMap.get(compBin);
        String jump = jumpMap.get(jumpBin);

        StringBuilder sb = new StringBuilder();
        if (!dest.isEmpty()) {
            sb.append(dest).append("=");
        }
        sb.append(comp);
        if (!jump.isEmpty()) {
            sb.append(";").append(jump);
        }

        return sb.toString();
    }
}
