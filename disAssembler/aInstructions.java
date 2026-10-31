
public class aInstructions {

    public static String parseA(String binStr, symbolTable st) {
        try {
            int val = Integer.parseInt(binStr.substring(1), 2);
            String label = st.getLabel(val);
            if (label != null) {
                return "@" + label;
            }
            return "@" + val;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor binario de instrucción A corrupto: " + binStr);
        }
    }
}
