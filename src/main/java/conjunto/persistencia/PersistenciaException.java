
package conjunto.persistencia;

/**
 * Error al leer o escribir el archivo de datos. Es una excepción CHEQUEADA a propósito:
 * obliga a quien llama a decidir qué hacer (mostrar un aviso, por ejemplo) en lugar de ignorarla.
 */
public class PersistenciaException extends Exception {

    private static final long serialVersionUID = 1L;

    public PersistenciaException(String mensaje) {
        super(mensaje);
    }

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
