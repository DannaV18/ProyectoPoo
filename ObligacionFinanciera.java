import java.time.LocalDate;
 
public class ObligacionFinanciera implements Pagable {
 
    public static final String PENDIENTE = "Pendiente";
    public static final String EN_MORA = "En mora";
    public static final String PAGADO = "Pagado";
 
    protected double valor;
    protected LocalDate fechaVencimiento;
    protected String estado;
    protected LocalDate fechaPago;
    protected MetodoPago metodoPago;
 
    public ObligacionFinanciera(double valor, LocalDate fechaVencimiento, String estado) {
        if (valor <= 0) {
            throw new IllegalArgumentException("El valor debe ser mayor a cero.");
        }
        this.valor = valor;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = estado;
    }
    
    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

        public LocalDate getFechaPago() {
        return fechaPago;
    }
 
    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public String getEstado() {
        if (estaPagada()) {
            return PAGADO;
        }
        return estaEnMora() ? EN_MORA : PENDIENTE;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
 
    @Override
    public boolean estaPagada() {
        return PAGADO.equalsIgnoreCase(estado);
    }
 
    public boolean estaEnMora() {
        return !estaPagada() && fechaVencimiento.isBefore(LocalDate.now());
    }
 
    public double calcularValor() {
        return estaPagada() ? 0 : valor;
    }
 
    @Override
    public void pagar(double monto, MetodoPago metodo) {
        if (estaPagada()) {
            throw new IllegalStateException("Esta obligación ya está pagada.");
        }
        double deuda = calcularValor();
        if (monto < deuda) {
            throw new IllegalArgumentException("El monto no cubre la deuda de $" + deuda);
        }
        this.estado = PAGADO;
        this.fechaPago = LocalDate.now();
        this.metodoPago = metodo;
    }
}
 
