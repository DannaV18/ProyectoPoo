import java.util.ArrayList;
import java.util.List;

public class Propiedad {
    private static int totalPropiedades = 0;

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
        totalPropiedades++;
    }

    public void registrarObligacion(ObligacionFinanciera obligacion) {
        obligaciones.add(obligacion);
    }

    public void registrarReserva(Reserva reserva) {
        reservas.add(reserva);
    }

    public List<ObligacionFinanciera> consultarDeudas() {
        List<ObligacionFinanciera> deudas = new ArrayList<>();
        for (ObligacionFinanciera ob : obligaciones) {
            if (!ob.estaPagada()) {
                deudas.add(ob);
            }
        }
        return deudas;
    }

    public double calcularDeudaTotal() {
        double total = 0;
        for (ObligacionFinanciera ob : obligaciones) {
            total += ob.calcularValor();
        }
        return total;
    }

    public boolean tieneDeudasEnMora() {
        for (ObligacionFinanciera ob : obligaciones) {
            if (ob.estaEnMora()) {
                return true;
            }
        }
        return false;
    }

    public static int getTotalPropiedades() {
        return totalPropiedades;
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
