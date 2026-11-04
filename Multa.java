import java.time.LocalDate;
 
public class Multa extends ObligacionFinanciera {
    
    private static final double RECARGO_MORA = 0.10;
 
    public Multa(double valor, LocalDate fechaVencimiento, String estado) {
        super(valor, fechaVencimiento, estado);
    }
 
    @Override
    public double calcularValor() {
        if (estaPagada()) {
            return 0;
        }
        return estaEnMora() ? valor * (1 + RECARGO_MORA) : valor;
    }
}
 
