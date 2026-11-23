package conjunto.modelo;

import java.time.LocalDate;

/**
 * Cobro por usar una zona común en una reserva. No tiene recargo por mora:
 * si no se paga, simplemente sigue pendiente.
 */
public class CargoReserva extends ObligacionFinanciera {

    public CargoReserva(String descripcion, double valor, LocalDate vencimiento) {
        super(descripcion, valor, vencimiento);
    }

    @Override
    protected double tasaMora() {
        return 0;
    }

    @Override
    public String getTipo() {
        return "Reserva";
    }

    @Override
    public String getCodigoArchivo() {
        return "RESERVA";
    }
}
