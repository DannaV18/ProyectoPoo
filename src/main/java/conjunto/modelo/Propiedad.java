package conjunto.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Casa o apartamento del conjunto. Su identificador (por ejemplo "Casa 4") es único
 * dentro del conjunto; la unicidad se controla en la capa de negocio.
 *
 * <p>La lista de obligaciones es privada: solo se puede cambiar por los métodos de esta clase,
 * y hacia afuera se devuelve una vista de solo lectura.</p>
 */
public class Propiedad {

    private final String identificador;
    private final String direccion;
    private final Residente residente;
    private final List<ObligacionFinanciera> obligaciones = new ArrayList<>();

    public Propiedad(String identificador, String direccion, Residente residente) {
        this.identificador = Validar.texto(identificador, "El identificador de la propiedad es obligatorio.");
        this.direccion = Validar.texto(direccion, "La dirección es obligatoria.");
        if (residente == null) {
            throw new IllegalArgumentException("Toda propiedad necesita un residente responsable.");
        }
        this.residente = residente;
    }

    public void registrarObligacion(ObligacionFinanciera obligacion) {
        if (obligacion == null) {
            throw new IllegalArgumentException("La obligación no puede estar vacía.");
        }
        obligaciones.add(obligacion);
    }

    /** Historial completo (pagadas y no pagadas), solo lectura. */
    public List<ObligacionFinanciera> getHistorial() {
        return Collections.unmodifiableList(obligaciones);
    }

    /** Deudas activas: pendientes o en mora (RF-03). */
    public List<ObligacionFinanciera> consultarDeudasActivas() {
        List<ObligacionFinanciera> activas = new ArrayList<>();
        for (ObligacionFinanciera ob : obligaciones) {
            if (!ob.estaPagada()) {
                activas.add(ob);
            }
        }
        return activas;
    }

    /** Total a pagar, aplicando el cálculo propio de cada tipo (RF-04). */
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

    public String getIdentificador() {
        return identificador;
    }

    public String getDireccion() {
        return direccion;
    }

    public Residente getResidente() {
        return residente;
    }

    @Override
    public String toString() {
        return identificador;
    }
}
