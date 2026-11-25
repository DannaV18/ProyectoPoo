package conjunto.modelo;

import java.time.LocalDate;

public abstract class ObligacionFinanciera implements Pagable {

    private final String descripcion;
    private final double valorBase;
    private final LocalDate fechaVencimiento;
    private boolean pagada;
    private LocalDate fechaPago;
    private MetodoPago metodoPago;
    private double valorPagado;

    protected ObligacionFinanciera(String descripcion, double valorBase, LocalDate fechaVencimiento) {
        this.descripcion = Validar.texto(descripcion, "La descripción de la obligación es obligatoria.");
        this.valorBase = Validar.positivo(valorBase, "El valor debe ser mayor a cero.");
        if (fechaVencimiento == null) {
            throw new IllegalArgumentException("La fecha de vencimiento es obligatoria.");
        }
        this.fechaVencimiento = fechaVencimiento;
    }

    protected abstract double tasaMora();

    public abstract String getTipo();

    public abstract String getCodigoArchivo();

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
        Validar.positivo(monto, "El monto a pagar debe ser un número mayor a cero.");
        double deuda = calcularValor();
        if (monto < deuda) {
            throw new IllegalArgumentException(
                    "El monto no cubre la deuda de $" + String.format("%.2f", deuda) + ".");
        }
        if (metodo == null) {
            throw new IllegalArgumentException("Debe indicar el método de pago.");
        }
        this.valorPagado = deuda;
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

    public double getValorPagado() {
        return valorPagado;
    }

    public void restaurarPago(LocalDate fecha, MetodoPago metodo, double valorPagado) {
        this.valorPagado = Validar.positivo(valorPagado, "El valor pagado guardado no es válido.");
        this.pagada = true;
        this.fechaPago = fecha;
        this.metodoPago = metodo;
    }
}
