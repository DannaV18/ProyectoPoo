import java.util.ArrayList;
import java.util.List;

public class Propiedad {
    private String identificador;
    private String direccion;
    private Residente propietario;
    private List<ObligacionFinanciera> obligaciones;
    private List<Reserva> reservas;

    public Propiedad(String identificador, String direccion, Residente propietario) {
        this.identificador = identificador;
        this.direccion = direccion;
        this.propietario = propietario;
        this.obligaciones = new ArrayList<>();
        this.reservas = new ArrayList<>();
    }

    public void registrarObligacion(ObligacionFinanciera obligacion) {
        obligaciones.add(obligacion);
    }

    public void registrarReserva(Reserva reserva) {
        reservas.add(reserva);
    }

    public List<ObligacionFinanciera> consultarDeudas() {
        List<ObligacionFinanciera> deudas = new ArrayList<>();
        for (ObligacionFinanciera ob : this.obligaciones) {
            if ("Pendiente".equalsIgnoreCase(ob.getEstado())) {
                deudas.add(ob);
            }
        }
        return deudas;
    }

    public String getIdentificador() {
        return identificador;
    }

    public String getDireccion() {
        return direccion;
    }

    public Residente getPropietario() {
        return propietario;
    }

    public List<ObligacionFinanciera> getObligaciones() {
        return obligaciones;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }
}