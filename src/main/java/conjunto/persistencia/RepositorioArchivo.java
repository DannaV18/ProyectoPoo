package conjunto.persistencia;

import conjunto.modelo.CanchaDeFutbol;
import conjunto.modelo.CargoReserva;
import conjunto.modelo.CuotaMensual;
import conjunto.modelo.EstadoReserva;
import conjunto.modelo.MetodoPago;
import conjunto.modelo.Multa;
import conjunto.modelo.ObligacionFinanciera;
import conjunto.modelo.Propiedad;
import conjunto.modelo.Reserva;
import conjunto.modelo.Residente;
import conjunto.modelo.SalonSocial;
import conjunto.modelo.ZonaBbq;
import conjunto.modelo.ZonaComun;
import conjunto.negocio.GestorConjunto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepositorioArchivo {

    private static final String VACIO = "-";

    private final Path ruta;

    public RepositorioArchivo(Path ruta) {
        this.ruta = ruta;
    }

    public GestorConjunto cargar() throws PersistenciaException {
        GestorConjunto gestor = new GestorConjunto();
        if (!Files.exists(ruta)) {
            guardar(gestor);
            return gestor;
        }

        List<String> lineas;
        try {
            lineas = Files.readAllLines(ruta, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo leer el archivo " + ruta + ".", e);
        }

        Map<String, Residente> residentesPorDocumento = new HashMap<>();
        for (int i = 0; i < lineas.size(); i++) {
            String linea = lineas.get(i).trim();
            if (linea.isEmpty() || linea.startsWith("#")) {
                continue;
            }
            try {
                procesarLinea(linea, gestor, residentesPorDocumento);
            } catch (RuntimeException e) {
                throw new PersistenciaException(
                        "El archivo está dañado en la línea " + (i + 1) + ": " + e.getMessage(), e);
            }
        }
        return gestor;
    }

    private void procesarLinea(String linea, GestorConjunto gestor, Map<String, Residente> residentes) {
        String[] c = linea.split(";", -1);
        switch (c[0]) {
            case "ZONA" -> gestor.restaurarZona(crearZona(c));
            case "PROPIEDAD" -> gestor.restaurarPropiedad(crearPropiedad(c, residentes));
            case "OBLIGACION" -> agregarObligacion(c, gestor);
            case "RESERVA" -> gestor.restaurarReserva(crearReserva(c, gestor));
            default -> throw new IllegalArgumentException("Tipo de registro desconocido: " + c[0]);
        }
    }

    private ZonaComun crearZona(String[] c) {
        requerirCampos(c, 6);
        String nombre = c[2];
        int capacidad = Integer.parseInt(c[3]);
        double costo = Double.parseDouble(c[4]);
        return switch (c[1]) {
            case "SALON" -> new SalonSocial(nombre, capacidad, costo, Boolean.parseBoolean(c[5]));
            case "BBQ" -> new ZonaBbq(nombre, capacidad, costo, Integer.parseInt(c[5]));
            case "CANCHA" -> new CanchaDeFutbol(nombre, capacidad, costo, Boolean.parseBoolean(c[5]));
            default -> throw new IllegalArgumentException("Tipo de zona desconocido: " + c[1]);
        };
    }

    private Propiedad crearPropiedad(String[] c, Map<String, Residente> residentes) {
        requerirCampos(c, 5);
        Residente residente = residentes.computeIfAbsent(c[4], doc -> new Residente(c[3], doc));
        return new Propiedad(c[1], c[2], residente);
    }

    private void agregarObligacion(String[] c, GestorConjunto gestor) {
        if (c.length != 9) {
            requerirCampos(c, 10);
        }
        Propiedad propiedad = gestor.buscarPropiedad(c[1])
                .orElseThrow(() -> new IllegalArgumentException("Propiedad inexistente: " + c[1]));
        ObligacionFinanciera ob = switch (c[2]) {
            case "CUOTA" -> new CuotaMensual(c[3], Double.parseDouble(c[4]), LocalDate.parse(c[5]));
            case "MULTA" -> new Multa(c[3], Double.parseDouble(c[4]), LocalDate.parse(c[5]));
            case "RESERVA" -> new CargoReserva(c[3], Double.parseDouble(c[4]), LocalDate.parse(c[5]));
            default -> throw new IllegalArgumentException("Tipo de obligación desconocido: " + c[2]);
        };
        if (Boolean.parseBoolean(c[6])) {
            double valorPagado = c.length == 10 ? Double.parseDouble(c[9]) : ob.getValorBase();
            ob.restaurarPago(LocalDate.parse(c[7]), MetodoPago.valueOf(c[8]), valorPagado);
        }
        propiedad.registrarObligacion(ob);
    }

    private Reserva crearReserva(String[] c, GestorConjunto gestor) {
        requerirCampos(c, 7);
        Propiedad propiedad = gestor.buscarPropiedad(c[1])
                .orElseThrow(() -> new IllegalArgumentException("Propiedad inexistente: " + c[1]));
        ZonaComun zona = gestor.buscarZona(c[2])
                .orElseThrow(() -> new IllegalArgumentException("Zona inexistente: " + c[2]));
        Reserva reserva = new Reserva(propiedad, zona,
                LocalDate.parse(c[3]), LocalTime.parse(c[4]), LocalTime.parse(c[5]));
        reserva.restaurarEstado(EstadoReserva.valueOf(c[6]));
        return reserva;
    }

    private static void requerirCampos(String[] c, int esperados) {
        if (c.length != esperados) {
            throw new IllegalArgumentException("se esperaban " + esperados + " campos y llegaron " + c.length);
        }
    }

    public void guardar(GestorConjunto gestor) throws PersistenciaException {
        List<String> lineas = new ArrayList<>();
        for (ZonaComun z : gestor.getZonas()) {
            lineas.add(String.join(";", "ZONA", z.getTipo(), z.getNombre(),
                    String.valueOf(z.getCapacidadMaxima()), String.valueOf(z.getCostoAlquiler()), z.getDetalle()));
        }
        for (Propiedad p : gestor.getPropiedades()) {
            lineas.add(String.join(";", "PROPIEDAD", p.getIdentificador(), p.getDireccion(),
                    p.getResidente().getNombre(), p.getResidente().getDocumento()));
            for (ObligacionFinanciera ob : p.getHistorial()) {
                lineas.add(aLineaObligacion(p, ob));
            }
        }
        for (Reserva r : gestor.getReservas()) {
            lineas.add(String.join(";", "RESERVA", r.getPropiedad().getIdentificador(),
                    r.getZona().getNombre(), r.getFecha().toString(), r.getHoraInicio().toString(),
                    r.getHoraFin().toString(), r.getEstado().name()));
        }

        Path temporal = ruta.resolveSibling(ruta.getFileName() + ".tmp");
        try {
            Files.write(temporal, lineas, StandardCharsets.UTF_8);
            moverReemplazando(temporal);
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo guardar el archivo " + ruta + ".", e);
        }
    }

    private String aLineaObligacion(Propiedad p, ObligacionFinanciera ob) {
        return String.join(";", "OBLIGACION", p.getIdentificador(), ob.getCodigoArchivo(), ob.getDescripcion(),
                String.valueOf(ob.getValorBase()), ob.getFechaVencimiento().toString(),
                String.valueOf(ob.estaPagada()),
                ob.getFechaPago() == null ? VACIO : ob.getFechaPago().toString(),
                ob.getMetodoPago() == null ? VACIO : ob.getMetodoPago().name(),
                ob.estaPagada() ? String.valueOf(ob.getValorPagado()) : VACIO);
    }

    private void moverReemplazando(Path temporal) throws IOException {
        try {
            Files.move(temporal, ruta, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporal, ruta, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
