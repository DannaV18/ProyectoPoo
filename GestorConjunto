package conjunto.negocio;

import conjunto.modelo.CargoReserva;
import conjunto.modelo.MetodoPago;
import conjunto.modelo.ObligacionFinanciera;
import conjunto.modelo.Propiedad;
import conjunto.modelo.Reserva;
import conjunto.modelo.ZonaComun;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Casos de uso del conjunto. Es el único punto de entrada para la interfaz y la persistencia:
 * ninguna de ellas toca las listas directamente.
 *
 * <p>Esta clase no sabe nada de ventanas ni de archivos. Si mañana cambiamos los archivos por una
 * base de datos, esta clase no cambia.</p>
 */
public class GestorConjunto {

    /** Días que se da para pagar el cargo de una reserva con costo. */
    private static final int DIAS_PLAZO_RESERVA = 15;

    private final List<Propiedad> propiedades = new ArrayList<>();
    private final List<ZonaComun> zonas = new ArrayList<>();
    private final List<Reserva> reservas = new ArrayList<>();

    // ---------- Propiedades (RF-01) ----------

    /** RF-01: no se permiten dos propiedades con el mismo identificador (sin importar mayúsculas). */
    public void registrarPropiedad(Propiedad propiedad) {
        if (buscarPropiedad(propiedad.getIdentificador()).isPresent()) {
            throw new ReglaNegocioException(
                    "Ya existe una propiedad con el identificador \"" + propiedad.getIdentificador() + "\".");
        }
        propiedades.add(propiedad);
    }

    public Optional<Propiedad> buscarPropiedad(String identificador) {
        return propiedades.stream()
                .filter(p -> p.getIdentificador().equalsIgnoreCase(identificador))
                .findFirst();
    }

    private Propiedad exigirPropiedad(String identificador) {
        return buscarPropiedad(identificador)
                .orElseThrow(() -> new ReglaNegocioException("No existe la propiedad \"" + identificador + "\"."));
    }

    // ---------- Obligaciones (RF-02, RF-03, RF-04, RF-05) ----------

    /** RF-02: asigna cuota, multa o reserva a una propiedad. Queda en su historial. */
    public void asignarObligacion(String identificadorPropiedad, ObligacionFinanciera obligacion) {
        exigirPropiedad(identificadorPropiedad).registrarObligacion(obligacion);
    }

    /** RF-03: solo devuelve las obligaciones pendientes o en mora. */
    public List<ObligacionFinanciera> consultarDeudasActivas(String identificadorPropiedad) {
        return exigirPropiedad(identificadorPropiedad).consultarDeudasActivas();
    }

    /** RF-04: total que debe la propiedad, con el cálculo propio de cada tipo. */
    public double calcularDeudaTotal(String identificadorPropiedad) {
        return exigirPropiedad(identificadorPropiedad).calcularDeudaTotal();
    }

    /** RF-05: registra el pago de una deuda específica de una propiedad. */
    public void registrarPago(String identificadorPropiedad, ObligacionFinanciera obligacion,
                              double monto, MetodoPago metodo) {
        Propiedad propiedad = exigirPropiedad(identificadorPropiedad);
        if (!propiedad.getHistorial().contains(obligacion)) {
            throw new ReglaNegocioException("La obligación no pertenece a la propiedad " + propiedad + ".");
        }
        obligacion.pagar(monto, metodo);
    }

    /** RF-06: solo propiedades con alguna deuda cuyo vencimiento ya pasó. */
    public List<Propiedad> generarReporteCarteraMorosa() {
        List<Propiedad> morosas = new ArrayList<>();
        for (Propiedad p : propiedades) {
            if (p.tieneDeudasEnMora()) {
                morosas.add(p);
            }
        }
        return morosas;
    }

    // ---------- Zonas comunes (RF-07) ----------

    /** RF-07: registra una zona nueva. Nombre, capacidad y costo los valida la propia zona. */
    public void configurarZonaComun(ZonaComun zona) {
        if (buscarZona(zona.getNombre()).isPresent()) {
            throw new ReglaNegocioException("Ya existe una zona llamada \"" + zona.getNombre() + "\".");
        }
        zonas.add(zona);
    }

    public Optional<ZonaComun> buscarZona(String nombre) {
        return zonas.stream()
                .filter(z -> z.getNombre().equalsIgnoreCase(nombre))
                .findFirst();
    }

    // ---------- Reservas (RF-08, RF-09) ----------

    /**
     * RF-08: el residente pide una zona en un horario. Se rechaza si la propiedad tiene
     * deudas en mora.
     * RF-09: el sistema verifica que no haya otra reserva activa que se cruce.
     *
     * <p>Si la zona tiene costo, se crea un cargo en el historial de la propiedad (RF-02).</p>
     */
    public Reserva solicitarReserva(String identificadorPropiedad, String nombreZona,
                                    LocalDate fecha, LocalTime inicio, LocalTime fin) {
        Propiedad propiedad = exigirPropiedad(identificadorPropiedad);
        ZonaComun zona = buscarZona(nombreZona)
                .orElseThrow(() -> new ReglaNegocioException("No existe la zona \"" + nombreZona + "\"."));

        if (propiedad.tieneDeudasEnMora()) {
            throw new ReglaNegocioException(
                    "La solicitud fue rechazada: la propiedad " + propiedad + " tiene deudas en mora.");
        }

        Reserva nueva = new Reserva(propiedad, zona, fecha, inicio, fin);
        for (Reserva existente : reservas) {
            if (existente.seSolapaCon(nueva)) {
                throw new ReglaNegocioException(
                        "La zona " + zona + " ya está reservada ese día en ese horario.");
            }
        }

        reservas.add(nueva);
        if (zona.tieneCosto()) {
            propiedad.registrarObligacion(new CargoReserva(
                    "Reserva " + zona.getNombre() + " el " + fecha,
                    zona.getCostoAlquiler(),
                    fecha.plusDays(DIAS_PLAZO_RESERVA)));
        }
        return nueva;
    }

    /** Reservas activas de una zona en una fecha, para consultar disponibilidad. */
    public List<Reserva> reservasDe(String nombreZona, LocalDate fecha) {
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.estaActiva() && r.getZona().getNombre().equalsIgnoreCase(nombreZona)
                    && r.getFecha().equals(fecha)) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    // ---------- Lectura para la interfaz y la persistencia ----------

    public List<Propiedad> getPropiedades() {
        return Collections.unmodifiableList(propiedades);
    }

    public List<ZonaComun> getZonas() {
        return Collections.unmodifiableList(zonas);
    }

    public List<Reserva> getReservas() {
        return Collections.unmodifiableList(reservas);
    }

    /**
     * Uso exclusivo de la persistencia al cargar datos: agrega sin repetir las reglas de
     * negocio, porque el archivo ya fue validado cuando se guardó.
     */
    public void restaurarPropiedad(Propiedad propiedad) {
        propiedades.add(propiedad);
    }

    public void restaurarZona(ZonaComun zona) {
        zonas.add(zona);
    }

    public void restaurarReserva(Reserva reserva) {
        reservas.add(reserva);
    }
}
