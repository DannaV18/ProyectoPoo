package conjunto.modelo;

import java.time.LocalDate;

/** Cuota de administración mensual. Si se paga tarde, recarga el 5 %. */
public class CuotaMensual extends ObligacionFinanciera {

    private static final double RECARGO_MORA = 0.05;

    public CuotaMensual(String descripcion, double valor, LocalDate vencimiento) {
        super(descripcion, valor, vencimiento);
    }

    @Override
    protected double tasaMora() {
        return RECARGO_MORA;
    }

    @Override
    public String getTipo() {
        return "Cuota mensual";
    }
}
