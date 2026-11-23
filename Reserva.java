package conjunto.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Uso de una zona común por una propiedad en una fecha y un rango de horas.
 * La regla de solapamiento vive aquí porque depende solo de los datos de la reserva.
 */
public class Reserva {

    private final Propiedad propiedad;
    private final ZonaComun zona;
    private final LocalDate fecha;
    private final LocalTime horaInicio;
    private final LocalTime horaFin;
    private EstadoReserva estado = EstadoReserva.ACTIVA;

    public Reserva(Propiedad propiedad, ZonaComun zona, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        if (propiedad == null || zona == null || fecha == null || horaInicio == null || horaFin == null) {
            throw new IllegalArgumentException("Todos los datos de la reserva son obligatorios.");
        }
        if (!horaFin.isAfter(horaInicio)) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio.");
        }
        this.propiedad = propiedad;
        this.zona = zona;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    /** Dos reservas chocan si son activas, de la misma zona y fecha, y sus horas se cruzan. */
    public boolean seSolapaCon(Reserva otra) {
        return estaActiva()
                && otra.estaActiva()
                && zona == otra.zona
                && fecha.equals(otra.fecha)
                && horaInicio.isBefore(otra.horaFin)
                && otra.horaInicio.isBefore(horaFin);
    }

    public void cancelar() {
        this.estado = EstadoReserva.CANCELADA;
    }

    public boolean estaActiva() {
        return estado == EstadoReserva.ACTIVA;
    }

    /** Solo lo usa la persistencia al leer un archivo cancelado. */
    public void restaurarEstado(EstadoReserva estadoGuardado) {
        this.estado = estadoGuardado;
    }

    public Propiedad getPropiedad() {
        return propiedad;
    }

    public ZonaComun getZona() {
        return zona;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public EstadoReserva getEstado() {
        return estado;
    }
}
