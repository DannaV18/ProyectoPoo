public interface Pagable {

    void pagar(double monto, MetodoPago metodo);

    boolean estaPagada();
}
