import java.time.LocalDate;

public class CuotaMensual extends ObligacionFinanciera {
    public CuotaMensual(double valor, LocalDate fechaVencimiento, String estado) {
        super(valor, fechaVencimiento, estado);
        

    }

    @Override 
    public double calcularValor() {
        return this.valor;
    }
    
}
