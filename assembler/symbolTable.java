
import java.util.HashMap;
import java.util.Map;

public class symbolTable {

    private static final int scAddress = 16384;
    private static final int kAddress = 24576;

    private final Map<String, Integer> map = new HashMap<>();
    private int ramAddress = 16;

    public symbolTable() {
        for (int i = 0; i <= 15; i++) {
            map.put("R" + i, i);
        }
        map.put("SP", 0);
        map.put("LCL", 1);
        map.put("ARG", 2);
        map.put("THIS", 3);
        map.put("THAT", 4);
        map.put("SCREEN", scAddress);
        map.put("KBD", kAddress);
    }

    public void addEntry(String symbol, int address) {
        if (symbol == null || symbol.isEmpty()) {
            throw new IllegalArgumentException("Etiqueta vacía.");
        }
        map.put(symbol, address);
    }

    public int getAddress(String symbol) {
        if (!map.containsKey(symbol)) {
            if (symbol.matches("^[0-9].*")) {
                throw new IllegalArgumentException("Símbolo inválido (inicia con número): " + symbol);
            }
            map.put(symbol, ramAddress++);
        }
        return map.get(symbol);
    }
}
