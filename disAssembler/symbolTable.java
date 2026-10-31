
import java.util.HashMap;
import java.util.Map;

public class symbolTable {

    private final Map<Integer, String> addressToLabel = new HashMap<>();

    public symbolTable() {
        addressToLabel.put(16384, "SCREEN");
        addressToLabel.put(24576, "KBD");
    }

    public void addLabel(int address, String label) {
        addressToLabel.put(address, label);
    }

    public String getLabel(int address) {
        return addressToLabel.get(address);
    }

    public boolean containsAddress(int address) {
        return addressToLabel.containsKey(address);
    }
}
