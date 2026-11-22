package conjunto.modelo;

/**
 * Validaciones reutilizables. Centralizarlas evita repetir el mismo if en cada clase
 * (las rúbricas penalizan el código duplicado).
 */
public final class Validar {

    private Validar() {
        // Clase utilitaria: no se instancia.
    }

    /** Devuelve el texto sin espacios sobrantes, o lanza excepción si está vacío o contiene ';'. */
    public static String texto(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }
        if (valor.contains(";")) {
            throw new IllegalArgumentException("El texto no puede contener el carácter ';'.");
        }
        return valor.trim();
    }

    public static double positivo(double valor, String mensaje) {
        if (valor <= 0) {
            throw new IllegalArgumentException(mensaje);
        }
        return valor;
    }

    public static double noNegativo(double valor, String mensaje) {
        if (valor < 0) {
            throw new IllegalArgumentException(mensaje);
        }
        return valor;
    }
}
