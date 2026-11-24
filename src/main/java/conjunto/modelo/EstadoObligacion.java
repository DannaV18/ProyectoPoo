
package conjunto.modelo;

/**
 * Enum en lugar de cadenas sueltas: el compilador impide escribir un estado mal ("Pendinte").
 */
public enum EstadoObligacion {
    PENDIENTE("pendiente"),
    EN_MORA("en mora"),
    PAGADO("pagado");

    private final String etiqueta;

    EstadoObligacion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
