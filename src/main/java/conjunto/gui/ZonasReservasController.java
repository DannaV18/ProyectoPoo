package conjunto.gui;

import conjunto.modelo.CanchaDeFutbol;
import conjunto.modelo.Piscina;
import conjunto.modelo.Propiedad;
import conjunto.modelo.Reserva;
import conjunto.modelo.SalonSocial;
import conjunto.modelo.ZonaBbq;
import conjunto.modelo.ZonaComun;
import conjunto.negocio.GestorConjunto;
import conjunto.negocio.ReglaNegocioException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ZonasReservasController {

    @FXML private ComboBox<String> comboTipoZona;
    @FXML private TextField txtNombreZona;
    @FXML private TextField txtCapacidad;
    @FXML private TextField txtCosto;
    @FXML private CheckBox chkEspecial;
    @FXML private TextField txtDatoNumerico;

    @FXML private TableView<ZonaComun> tablaZonas;
    @FXML private TableColumn<ZonaComun, String> colNombreZona;
    @FXML private TableColumn<ZonaComun, String> colTipoZona;
    @FXML private TableColumn<ZonaComun, String> colCapacidad;
    @FXML private TableColumn<ZonaComun, String> colCosto;

    @FXML private ComboBox<Propiedad> comboPropiedad;
    @FXML private ComboBox<ZonaComun> comboZona;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtInicio;
    @FXML private TextField txtFin;

    @FXML private TableView<Reserva> tablaReservas;
    @FXML private TableColumn<Reserva, String> colRPropiedad;
    @FXML private TableColumn<Reserva, String> colRZona;
    @FXML private TableColumn<Reserva, String> colRFecha;
    @FXML private TableColumn<Reserva, String> colRHorario;
    @FXML private TableColumn<Reserva, String> colREstado;

    @FXML private Label lblMensaje;

    private GestorConjunto gestor;
    private Runnable alCambiarDatos;

    public void inicializar(GestorConjunto gestor, Runnable alCambiarDatos) {
        this.gestor = gestor;
        this.alCambiarDatos = alCambiarDatos;

        comboTipoZona.getItems().setAll("SALON", "BBQ", "CANCHA", "PISCINA");
        comboTipoZona.getSelectionModel().selectFirst();
        comboTipoZona.valueProperty().addListener((obs, anterior, nuevo) -> ajustarCamposPorTipo(nuevo));
        ajustarCamposPorTipo(comboTipoZona.getValue());

        colNombreZona.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colTipoZona.setCellValueFactory(c -> new SimpleStringProperty(nombreTipo(c.getValue().getTipo())));
        colCapacidad.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getCapacidadMaxima())));
        colCosto.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().tieneCosto() ? String.format(Locale.ROOT, "$%,.0f", c.getValue().getCostoAlquiler()) : "Sin costo"));

        colRPropiedad.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPropiedad().getIdentificador()));
        colRZona.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getZona().getNombre()));
        colRFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFecha().toString()));
        colRHorario.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getHoraInicio() + " - " + c.getValue().getHoraFin()));
        colREstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().estaActiva() ? "Activa" : "Cancelada"));

        refrescar();
    }

    public void refrescar() {
        tablaZonas.setItems(FXCollections.observableArrayList(gestor.getZonas()));
        tablaReservas.setItems(FXCollections.observableArrayList(gestor.getReservas()));

        Propiedad propiedadPrevia = comboPropiedad.getValue();
        comboPropiedad.setItems(FXCollections.observableArrayList(gestor.getPropiedades()));
        comboPropiedad.setValue(propiedadPrevia);

        ZonaComun zonaPrevia = comboZona.getValue();
        comboZona.setItems(FXCollections.observableArrayList(gestor.getZonas()));
        comboZona.setValue(zonaPrevia);
    }

    @FXML
    private void onCrearZona() {
        String tipo = comboTipoZona.getValue();
        try {
            String nombre = txtNombreZona.getText();
            int capacidad = Integer.parseInt(txtCapacidad.getText().trim());
            double costo = Double.parseDouble(txtCosto.getText().trim());
            ZonaComun zona = switch (tipo) {
                case "SALON" -> new SalonSocial(nombre, capacidad, costo, chkEspecial.isSelected());
                case "BBQ" -> new ZonaBbq(nombre, capacidad, costo, Integer.parseInt(txtDatoNumerico.getText().trim()));
                case "PISCINA" -> new Piscina(nombre, capacidad, costo,
                        Double.parseDouble(txtDatoNumerico.getText().trim()), chkEspecial.isSelected());
                default -> new CanchaDeFutbol(nombre, capacidad, costo, chkEspecial.isSelected());
            };
            gestor.configurarZonaComun(zona);
            txtNombreZona.clear();
            txtCapacidad.clear();
            txtCosto.clear();
            txtDatoNumerico.clear();
            lblMensaje.setText("Zona " + zona + " registrada.");
            alCambiarDatos.run();
        } catch (NumberFormatException e) {
            Dialogos.aviso("La capacidad, el costo, las parrillas y la profundidad deben ser números válidos.");
        } catch (IllegalArgumentException | ReglaNegocioException e) {
            Dialogos.aviso(e.getMessage());
        }
    }

    @FXML
    private void onSolicitarReserva() {
        Propiedad propiedad = comboPropiedad.getValue();
        ZonaComun zona = comboZona.getValue();
        LocalDate fecha = dpFecha.getValue();
        if (propiedad == null || zona == null || fecha == null) {
            Dialogos.aviso("Seleccione propiedad, zona y fecha.");
            return;
        }
        try {
            LocalTime inicio = LocalTime.parse(txtInicio.getText().trim());
            LocalTime fin = LocalTime.parse(txtFin.getText().trim());
            gestor.solicitarReserva(propiedad.getIdentificador(), zona.getNombre(), fecha, inicio, fin);
            lblMensaje.setText("Reserva aprobada para " + zona + " el " + fecha + ".");
            alCambiarDatos.run();
        } catch (DateTimeException e) {
            Dialogos.aviso("Escriba las horas en formato HH:mm, por ejemplo 14:30.");
        } catch (IllegalArgumentException | ReglaNegocioException e) {
            Dialogos.aviso(e.getMessage());
        }
    }

    @FXML
    private void onCancelarReserva() {
        Reserva reserva = tablaReservas.getSelectionModel().getSelectedItem();
        Propiedad solicitante = comboPropiedad.getValue();
        if (reserva == null || solicitante == null) {
            Dialogos.aviso("Seleccione una reserva en la tabla y la propiedad que la cancela.");
            return;
        }
        try {
            gestor.cancelarReserva(solicitante.getIdentificador(), reserva);
            lblMensaje.setText("Reserva cancelada. El horario quedó libre.");
            alCambiarDatos.run();
        } catch (ReglaNegocioException e) {
            Dialogos.aviso(e.getMessage());
        }
    }

    @FXML
    private void onVerDisponibilidad() {
        ZonaComun zona = comboZona.getValue();
        LocalDate fecha = dpFecha.getValue();
        if (zona == null || fecha == null) {
            Dialogos.aviso("Seleccione zona y fecha para ver los horarios.");
            return;
        }
        List<Reserva> ocupadas = gestor.reservasDe(zona.getNombre(), fecha);
        if (ocupadas.isEmpty()) {
            Dialogos.info("Disponibilidad", zona + " está libre ese día.");
            return;
        }
        String texto = ocupadas.stream()
                .map(r -> r.getHoraInicio() + " a " + r.getHoraFin() + " (" + r.getPropiedad().getIdentificador() + ")")
                .collect(Collectors.joining("\n"));
        Dialogos.info("Horarios ocupados", texto);
    }

    private void ajustarCamposPorTipo(String tipo) {
        boolean usaNumero = "BBQ".equals(tipo) || "PISCINA".equals(tipo);
        boolean usaCasilla = !"BBQ".equals(tipo);
        txtDatoNumerico.setVisible(usaNumero);
        txtDatoNumerico.setManaged(usaNumero);
        txtDatoNumerico.setPromptText("PISCINA".equals(tipo) ? "Profundidad (m)" : "Número de parrillas");
        chkEspecial.setVisible(usaCasilla);
        chkEspecial.setManaged(usaCasilla);
        chkEspecial.setText(switch (tipo) {
            case "SALON" -> "Tiene cocina";
            case "CANCHA" -> "Tiene iluminación";
            case "PISCINA" -> "Requiere certificado de salud";
            default -> "";
        });
    }

    private static String nombreTipo(String codigo) {
        return switch (codigo) {
            case "SALON" -> "Salón social";
            case "BBQ" -> "Zona BBQ";
            case "PISCINA" -> "Piscina";
            default -> "Cancha";
        };
    }
}
