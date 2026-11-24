
package conjunto.modelo;

import java.time.LocalDate;

/** Sanción impuesta a la propiedad. Si no se paga a tiempo, recarga el 10 %. */
public class Multa extends ObligacionFinanciera {

    private static final double RECARGO_MORA = 0.10;

    public Multa(String descripcion, double valor, LocalDate vencimiento) {
        super(descripcion, valor, vencimiento);
    }

    @Override
    protected double tasaMora() {
        return RECARGO_MORA;
    }

    @Override
    public String getTipo() {
        return "Multa";
    }

    @Override
    public String getCodigoArchivo() {
        return "MULTA";
    }
}
