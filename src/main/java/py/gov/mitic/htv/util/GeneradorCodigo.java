package py.gov.mitic.htv.util;

import java.security.SecureRandom;

public class GeneradorCodigo {
    private static final String LETRAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMEROS = "0123456789";
    private static final String CARACTERES_PERMITIDOS = LETRAS + NUMEROS;
    private static final int LONGITUD_CODIGO = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generarCodigoAlfanumerico() {
        String codigoGenerado;
        boolean contieneLetra;
        boolean contieneNumero;

        do {
            StringBuilder sb = new StringBuilder(LONGITUD_CODIGO);
            for (int i = 0; i < LONGITUD_CODIGO; i++) {
                int indice = RANDOM.nextInt(CARACTERES_PERMITIDOS.length());
                sb.append(CARACTERES_PERMITIDOS.charAt(indice));
            }
            codigoGenerado = sb.toString();

            // Validación del código
            contieneLetra = false;
            contieneNumero = false;
            for (char c : codigoGenerado.toCharArray()) {
                if (LETRAS.indexOf(c) != -1) {
                    contieneLetra = true;
                } else if (NUMEROS.indexOf(c) != -1) {
                    contieneNumero = true;
                }
            }

        } while (!contieneLetra || !contieneNumero);

        return codigoGenerado;
    }
}
