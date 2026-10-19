import java.time.LocalDate;

public class ObligacionFinanciera {

    protected double valor;
    protected LocalDate fechaVencimiento;
    protected String estado;

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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public ObligacionFinanciera(double valor, LocalDate fechaVencimiento, String estado) {
        this.valor = valor;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = estado;
    }

    public double calcularValor() {
        return this.valor;
    }


}