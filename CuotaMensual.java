import java.time.LocalDate;
 
public class CuotaMensual extends ObligacionFinanciera {
    
    private static final double RECARGO_MORA = 0.05;
 
    public CuotaMensual(double valor, LocalDate fechaVencimiento, String estado) {
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
 
