
package conjunto.negocio;

/**
 * Se lanza cuando una operación viola una regla del conjunto (por ejemplo, reservar con deudas en mora).
 * Es distinta de IllegalArgumentException: aquella indica datos mal formados; esta indica
 * que los datos son válidos pero la regla no permite la operación. La interfaz la muestra tal cual al usuario.
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
