package conjunto.modelo;

import java.time.LocalDate;

/**
 * Clase abstracta: todo cobro tiene descripción, valor base, vencimiento y un estado.
 * No se puede instanciar directamente, porque "un cobro genérico" no tiene sentido en el negocio.
 *
 * <p>Usa el patrón "método plantilla": calcularValor() está definido aquí y delega en
 * tasaMora(), que cada subclase sobrescribe. Así cada tipo aplica su propia regla
 * sin que el código de afuera pregunte de qué tipo es (RF-04).</p>
 */
public abstract class ObligacionFinanciera implements Pagable {

    private final String descripcion;
    private final double valorBase;
    private final LocalDate fechaVencimiento;
    private boolean pagada;
    private LocalDate fechaPago;
    private MetodoPago metodoPago;

    protected ObligacionFinanciera(String descripcion, double valorBase, LocalDate fechaVencimiento) {
        this.descripcion = Validar.texto(descripcion, "La descripción de la obligación es obligatoria.");
        this.valorBase = Validar.positivo(valorBase, "El valor debe ser mayor a cero.");
        if (fechaVencimiento == null) {
            throw new IllegalArgumentException("La fecha de vencimiento es obligatoria.");
        }
        this.fechaVencimiento = fechaVencimiento;
    }

    /** Cada tipo de obligación define su propio recargo por mora (0 si no tiene). */
    protected abstract double tasaMora();

    /** Tipo legible para la interfaz, por ejemplo "Cuota mensual". */
    public abstract String getTipo();

    public double calcularValor() {
        if (estaPagada()) {
            return 0;
        }
        return estaEnMora() ? valorBase * (1 + tasaMora()) : valorBase;
    }

    public EstadoObligacion getEstado() {
        if (estaPagada()) {
            return EstadoObligacion.PAGADO;
        }
        return estaEnMora() ? EstadoObligacion.EN_MORA : EstadoObligacion.PENDIENTE;
    }

    public boolean estaEnMora() {
        return !pagada && fechaVencimiento.isBefore(LocalDate.now());
    }

    @Override
    public boolean estaPagada() {
        return pagada;
    }

    @Override
    public void pagar(double monto, MetodoPago metodo) {
        if (pagada) {
            throw new IllegalStateException("Esta obligación ya está pagada.");
        }
        double deuda = calcularValor();
        if (monto < deuda) {
            throw new IllegalArgumentException(
                    "El monto no cubre la deuda de $" + String.format("%.2f", deuda) + ".");
        }
        if (metodo == null) {
            throw new IllegalArgumentException("Debe indicar el método de pago.");
        }
        this.pagada = true;
        this.fechaPago = LocalDate.now();
        this.metodoPago = metodo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getValorBase() {
        return valorBase;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    /**
     * Restaura el estado de pago al leer el archivo. Solo lo usa la capa de persistencia.
     */
    public void restaurarPago(LocalDate fecha, MetodoPago metodo) {
        this.pagada = true;
        this.fechaPago = fecha;
        this.metodoPago = metodo;
    }
}
