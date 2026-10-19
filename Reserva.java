import java.time.LocalDate;
import java.time.LocalTime;
public class Reserva {
    private LocalDate fechaReserva;
    private LocalTime HoraInicio;
    private LocalTime HoraFin;
    private String estado;
    private ZonaComun zonaComun;

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public LocalTime getHoraInicio() {
        return HoraInicio;
    }

    public LocalTime getHoraFin() {
        return HoraFin;
    }

    public String getEstado() {
        return estado;
    }

    public ZonaComun getZonaComun() {
        return zonaComun;
    }

    public Reserva(LocalDate fechaReserva, LocalTime horaInicio, LocalTime horaFin, String estado, ZonaComun zonaComun) {
        this.fechaReserva = fechaReserva;
        HoraInicio = horaInicio;
        HoraFin = horaFin;
        this.estado = estado;
        this.zonaComun = zonaComun;
    }
    
}
