package conjunto;

import conjunto.modelo.CanchaDeFutbol;
import conjunto.modelo.Comprobante;
import conjunto.modelo.CuotaMensual;
import conjunto.modelo.MetodoPago;
import conjunto.modelo.Multa;
import conjunto.modelo.ObligacionFinanciera;
import conjunto.modelo.Propiedad;
import conjunto.modelo.Reserva;
import conjunto.modelo.Residente;
import conjunto.modelo.SalonSocial;
import conjunto.modelo.ZonaBbq;
import conjunto.negocio.GestorConjunto;
import conjunto.negocio.ReglaNegocioException;
import conjunto.persistencia.PersistenciaException;
import conjunto.persistencia.RepositorioArchivo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Pruebas sencillas sin JUnit. Ejecutar: java -cp target/classes:target/test-classes conjunto.Verificacion
 * Cada comprobación imprime OK o FALLA, y el programa termina con código 1 si alguna falla.
 */
public class Verificacion {

    private static int fallos = 0;
    private static int total = 0;

    public static void main(String[] args) throws Exception {
        pruebaRF01DuplicadosSinImportarMayusculas();
        pruebaRF04RecargosPorTipo();
        pruebaRF05PagoDejaSaldoEnCero();
        pruebaRF06SoloVencidas();
        pruebaRF08RechazaReservaConMora();
        pruebaRF09RechazaSolapamiento();
        pruebaRF07ZonaSinCostoYConCosto();
        pruebaRNF01IdaYVuelta();
        pruebaRNF02ArchivoInexistenteSeCrea();
        pruebaRNF02ArchivoDanadoAvisaLinea();
        pruebaRF11HistorialSoloPagadasDeLaPropiedad();
        pruebaRF12SoloElDuenoCancelaYLiberaHorario();
        pruebaRF13ComprobanteDeUnPagoRealizado();

        System.out.println();
        System.out.println(total - fallos + " de " + total + " comprobaciones correctas.");
        if (fallos > 0) {
            System.exit(1);
        }
    }

    private static void comprobar(boolean condicion, String descripcion) {
        total++;
        if (condicion) {
            System.out.println("OK    " + descripcion);
        } else {
            fallos++;
            System.out.println("FALLA " + descripcion);
        }
    }

    private static Residente residente() {
        return new Residente("Ana Pérez", "1001");
    }

    private static void pruebaRF01DuplicadosSinImportarMayusculas() {
        GestorConjunto g = new GestorConjunto();
        g.registrarPropiedad(new Propiedad("Casa 4", "Calle 1", residente()));
        boolean rechazo = false;
        try {
            g.registrarPropiedad(new Propiedad("casa 4", "Calle 2", residente()));
        } catch (ReglaNegocioException e) {
            rechazo = true;
        }
        comprobar(rechazo, "RF-01 no permite dos propiedades con el mismo identificador");
    }

    private static void pruebaRF04RecargosPorTipo() {
        GestorConjunto g = new GestorConjunto();
        g.registrarPropiedad(new Propiedad("Casa 1", "Calle 1", residente()));
        LocalDate ayer = LocalDate.now().minusDays(1);
        g.asignarObligacion("Casa 1", new CuotaMensual("Cuota", 100000, ayer));
        g.asignarObligacion("Casa 1", new Multa("Ruido", 100000, ayer));
        double total = g.calcularDeudaTotal("Casa 1");
        // Cuota vencida: 100000 * 1.05 = 105000. Multa vencida: 100000 * 1.10 = 110000.
        comprobar(Math.abs(total - 215000) < 0.01, "RF-04 aplica 5 % a la cuota y 10 % a la multa vencidas (total " + total + ")");
    }

    private static void pruebaRF05PagoDejaSaldoEnCero() {
        GestorConjunto g = new GestorConjunto();
        g.registrarPropiedad(new Propiedad("Casa 2", "Calle 1", residente()));
        CuotaMensual cuota = new CuotaMensual("Cuota mayo", 200000, LocalDate.now().plusDays(10));
        g.asignarObligacion("Casa 2", cuota);
        g.registrarPago("Casa 2", cuota, cuota.calcularValor(), MetodoPago.PSE);
        comprobar(cuota.calcularValor() == 0 && cuota.getEstado().getEtiqueta().equals("pagado"),
                "RF-05 tras pagar, el saldo queda en cero y el estado es pagado");
    }

    private static void pruebaRF06SoloVencidas() {
        GestorConjunto g = new GestorConjunto();
        g.registrarPropiedad(new Propiedad("Casa 3", "Calle 1", residente()));
        g.registrarPropiedad(new Propiedad("Casa 5", "Calle 1", new Residente("Luis", "2002")));
        g.asignarObligacion("Casa 3", new CuotaMensual("Vencida", 50000, LocalDate.now().minusDays(3)));
        g.asignarObligacion("Casa 5", new CuotaMensual("Vigente", 50000, LocalDate.now().plusDays(3)));
        comprobar(g.generarReporteCarteraMorosa().size() == 1 && g.generarReporteCarteraMorosa().get(0).getIdentificador().equals("Casa 3"),
                "RF-06 el reporte de morosos solo incluye deudas con vencimiento pasado");
    }

    private static void pruebaRF08RechazaReservaConMora() {
        GestorConjunto g = new GestorConjunto();
        g.registrarPropiedad(new Propiedad("Casa 6", "Calle 1", residente()));
        g.configurarZonaComun(new SalonSocial("Salón", 40, 0, true));
        g.asignarObligacion("Casa 6", new CuotaMensual("Vencida", 50000, LocalDate.now().minusDays(2)));
        boolean rechazo = false;
        try {
            g.solicitarReserva("Casa 6", "Salón", LocalDate.now().plusDays(5), LocalTime.of(10, 0), LocalTime.of(12, 0));
        } catch (ReglaNegocioException e) {
            rechazo = true;
        }
        comprobar(rechazo, "RF-08 rechaza la reserva si la propiedad tiene deudas en mora");
    }

    private static void pruebaRF09RechazaSolapamiento() {
        GestorConjunto g = new GestorConjunto();
        g.registrarPropiedad(new Propiedad("Casa 7", "Calle 1", residente()));
        g.registrarPropiedad(new Propiedad("Casa 8", "Calle 2", new Residente("Mario", "3003")));
        g.configurarZonaComun(new ZonaBbq("BBQ", 10, 0, 2));
        LocalDate dia = LocalDate.now().plusDays(7);
        g.solicitarReserva("Casa 7", "BBQ", dia, LocalTime.of(10, 0), LocalTime.of(12, 0));

        boolean choque = false;
        try {
            g.solicitarReserva("Casa 8", "BBQ", dia, LocalTime.of(11, 0), LocalTime.of(13, 0));
        } catch (ReglaNegocioException e) {
            choque = true;
        }
        boolean contiguaPermitida = true;
        try {
            g.solicitarReserva("Casa 8", "BBQ", dia, LocalTime.of(12, 0), LocalTime.of(14, 0));
        } catch (ReglaNegocioException e) {
            contiguaPermitida = false;
        }
        comprobar(choque, "RF-09 rechaza una reserva que se cruza con otra aprobada");
        comprobar(contiguaPermitida, "RF-09 permite una reserva que empieza justo cuando termina otra");
    }

    private static void pruebaRF07ZonaSinCostoYConCosto() {
        GestorConjunto g = new GestorConjunto();
        g.configurarZonaComun(new CanchaDeFutbol("Cancha", 22, 0, true));
        g.configurarZonaComun(new SalonSocial("Salón", 80, 150000, false));
        comprobar(!g.buscarZona("Cancha").orElseThrow().tieneCosto() && g.buscarZona("Salón").orElseThrow().tieneCosto(),
                "RF-07 cada zona queda con su nombre, capacidad y si tiene o no costo");
    }

    private static void pruebaRNF01IdaYVuelta() throws PersistenciaException, IOException {
        Path archivo = crearRuta("ida_vuelta");
        GestorConjunto g = new GestorConjunto();
        g.configurarZonaComun(new SalonSocial("Salón", 80, 150000, true));
        g.registrarPropiedad(new Propiedad("Casa 9", "Calle 9", new Residente("Sara", "4004")));
        g.asignarObligacion("Casa 9", new Multa("Ruido", 80000, LocalDate.now().minusDays(1)));
        RepositorioArchivo repo = new RepositorioArchivo(archivo);
        repo.guardar(g);

        GestorConjunto leido = repo.cargar();
        comprobar(leido.getPropiedades().size() == 1 && leido.getZonas().size() == 1
                        && leido.calcularDeudaTotal("Casa 9") > 80000,
                "RNF-01 al volver a cargar, propiedades, zonas y deudas siguen disponibles");
    }

    private static void pruebaRNF02ArchivoInexistenteSeCrea() throws PersistenciaException, IOException {
        Path archivo = crearRuta("nuevo");
        Files.deleteIfExists(archivo);
        GestorConjunto g = new RepositorioArchivo(archivo).cargar();
        comprobar(Files.exists(archivo) && g.getPropiedades().isEmpty(),
                "RNF-02 si el archivo no existe, lo crea y el programa sigue funcionando");
    }

    private static void pruebaRNF02ArchivoDanadoAvisaLinea() throws IOException {
        Path archivo = crearRuta("danado");
        Files.writeString(archivo, "PROPIEDAD;Casa 1;Calle 1;Ana;1001\nBASURA;x\n", StandardCharsets.UTF_8);
        boolean avisoLinea = false;
        try {
            new RepositorioArchivo(archivo).cargar();
        } catch (PersistenciaException e) {
            avisoLinea = e.getMessage().contains("línea 2");
        }
        comprobar(avisoLinea, "RNF-02 un archivo dañado produce un aviso con el número de línea, sin cerrar el programa");
    }

    private static void pruebaRF11HistorialSoloPagadasDeLaPropiedad() {
        GestorConjunto g = new GestorConjunto();
        g.registrarPropiedad(new Propiedad("Casa 10", "Calle 1", residente()));
        g.registrarPropiedad(new Propiedad("Casa 11", "Calle 2", new Residente("Eva", "5005")));
        CuotaMensual pagada = new CuotaMensual("Cuota abril", 100000, LocalDate.now().plusDays(5));
        g.asignarObligacion("Casa 10", pagada);
        g.asignarObligacion("Casa 10", new Multa("Pendiente", 30000, LocalDate.now().plusDays(5)));
        g.asignarObligacion("Casa 11", new CuotaMensual("Otra casa", 90000, LocalDate.now().plusDays(5)));
        g.registrarPago("Casa 10", pagada, 100000, MetodoPago.TRANSFERENCIA);

        List<ObligacionFinanciera> historial = g.historialDePagos("Casa 10");
        comprobar(historial.size() == 1 && historial.get(0) == pagada && historial.get(0).getFechaPago() != null,
                "RF-11 el historial solo muestra pagos de la propiedad, con fecha de pago");
    }

    private static void pruebaRF12SoloElDuenoCancelaYLiberaHorario() {
        GestorConjunto g = new GestorConjunto();
        g.registrarPropiedad(new Propiedad("Casa 12", "Calle 1", residente()));
        g.registrarPropiedad(new Propiedad("Casa 13", "Calle 2", new Residente("Iván", "6006")));
        g.configurarZonaComun(new ZonaBbq("BBQ 2", 10, 0, 1));
        LocalDate dia = LocalDate.now().plusDays(9);
        Reserva reserva = g.solicitarReserva("Casa 12", "BBQ 2", dia, LocalTime.of(9, 0), LocalTime.of(11, 0));

        boolean otroNoPuede = false;
        try {
            g.cancelarReserva("Casa 13", reserva);
        } catch (ReglaNegocioException e) {
            otroNoPuede = true;
        }
        g.cancelarReserva("Casa 12", reserva);
        boolean horarioLibre = g.reservasDe("BBQ 2", dia).isEmpty();
        Reserva otra = g.solicitarReserva("Casa 13", "BBQ 2", dia, LocalTime.of(9, 0), LocalTime.of(11, 0));

        comprobar(otroNoPuede, "RF-12 otra propiedad no puede cancelar la reserva");
        comprobar(horarioLibre && otra.estaActiva(), "RF-12 al cancelar, el horario queda libre para otra solicitud");
    }

    private static void pruebaRF13ComprobanteDeUnPagoRealizado() {
        GestorConjunto g = new GestorConjunto();
        g.registrarPropiedad(new Propiedad("Casa 14", "Calle 1", residente()));
        CuotaMensual cuota = new CuotaMensual("Cuota junio", 120000, LocalDate.now().plusDays(3));
        g.asignarObligacion("Casa 14", cuota);
        boolean sinPagoNoHay = false;
        try {
            g.generarComprobante("Casa 14", cuota);
        } catch (IllegalStateException e) {
            sinPagoNoHay = true;
        }
        g.registrarPago("Casa 14", cuota, 120000, MetodoPago.EFECTIVO);
        Comprobante c = g.generarComprobante("Casa 14", cuota);
        boolean esperado = c.getNumero().equals("CMP-CASA14-001") && c.getMonto() == 120000
                && c.getMetodo() == MetodoPago.EFECTIVO && c.comoTexto().contains("CMP-CASA14-001");
        comprobar(sinPagoNoHay && esperado, "RF-13 el comprobante solo existe para pagos realizados y tiene número, monto, fecha y método");
    }

    private static Path crearRuta(String nombre) throws IOException {
        Path dir = Files.createTempDirectory("conjunto-verificacion");
        return dir.resolve(nombre + ".txt");
    }
}
