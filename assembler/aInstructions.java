
public class aInstructions {

    public static String translate(String val) {
        try {
            int num = Integer.parseInt(val);
            if (num < 0 || num > 32767) {
                throw new IllegalArgumentException("Valor fuera de rango (0-32767): " + num);
            }
            return String.format("%16s", Integer.toBinaryString(num)).replace(' ', '0');
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Instrucción A no numérica o no resuelta: " + val);
        }
    }
}
