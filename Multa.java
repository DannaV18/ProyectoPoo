import java.time.LocalDate;

public class Multa extends ObligacionFinanciera {

    public Multa(double valor, LocalDate fechaVencimiento, String estado) {
        super(valor, fechaVencimiento, estado);
    }

    @Override
    public double calcularValor() {
        return this.valor;
    }
}