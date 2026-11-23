package conjunto.modelo;

/**
 * Contrato de cualquier cosa que se puede pagar. Las interfaces describen QUÉ se puede
 * hacer, sin decir CÓMO; por eso el negocio puede pagar cualquier obligación sin conocer su tipo.
 */
public interface Pagable {

    void pagar(double monto, MetodoPago metodo);

    boolean estaPagada();
}
