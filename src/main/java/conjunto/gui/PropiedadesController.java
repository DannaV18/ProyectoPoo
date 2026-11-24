package conjunto.gui;

import conjunto.modelo.CargoReserva;
import conjunto.modelo.Comprobante;
import conjunto.modelo.CuotaMensual;
import conjunto.modelo.MetodoPago;
import conjunto.modelo.Multa;
import conjunto.modelo.ObligacionFinanciera;
import conjunto.modelo.Propiedad;
import conjunto.modelo.Residente;
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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Controlador de la pestaña de propiedades y cobros. No tiene reglas de negocio:
 * valida que los datos sean legibles (RF-10) y delega todo lo demás en GestorConjunto.
 */
public class PropiedadesController {

    @FXML private TextField txtIdentificador;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtNombreResidente;
    @FXML private TextField txtDocumento;

    @FXML private TableView<Propiedad> tablaPropiedades;
    @FXML private TableColumn<Propiedad, String> colIdentificador;
    @FXML private TableColumn<Propiedad, String> colDireccion;
    @FXML private TableColumn<Propiedad, String> colResidente;
    @FXML private TableColumn<Propiedad, String> colDeuda;

    @FXML private Label lblPropiedadSeleccionada;
    @FXML private ComboBox<String> comboTipo;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtValor;
    @FXML private DatePicker dpVencimiento;
    @FXML private CheckBox chkSoloActivas;

    @FXML private TableView<ObligacionFinanciera> tablaObligaciones;
    @FXML private TableColumn<ObligacionFinanciera, String> colTipo;
    @FXML private TableColumn<ObligacionFinanciera, String> colDescripcion;
    @FXML private TableColumn<ObligacionFinanciera, String> colValorPendiente;
    @FXML private TableColumn<ObligacionFinanciera, String> colVencimiento;
    @FXML private TableColumn<ObligacionFinanciera, String> colEstado;

    @FXML private TextField txtMontoPago;
    @FXML private ComboBox<MetodoPago> comboMetodo;
    @FXML private Label lblMensaje;

    private GestorConjunto gestor;
    private Runnable alCambiarDatos;

    public void inicializar(GestorConjunto gestor, Runnable alCambiarDatos) {
        this.gestor = gestor;
        this.alCambiarDatos = alCambiarDatos;

        comboTipo.getItems().setAll("Cuota mensual", "Multa", "Reserva");
        comboTipo.getSelectionModel().selectFirst();
        comboMetodo.getItems().setAll(MetodoPago.values());
        comboMetodo.getSelectionModel().selectFirst();

        colIdentificador.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdentificador()));
        colDireccion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDireccion()));
        colResidente.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getResidente().getNombre()));
        colDeuda.setCellValueFactory(c -> new SimpleStringProperty(
                formatoDinero(gestor.calcularDeudaTotal(c.getValue().getIdentificador()))));

        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo()));
        colDescripcion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescripcion()));
        colValorPendiente.setCellValueFactory(c -> new SimpleStringProperty(formatoDinero(c.getValue().calcularValor())));
        colVencimiento.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaVencimiento().toString()));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado().getEtiqueta()));

        tablaPropiedades.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, nueva) -> mostrarObligaciones(nueva));

        // RF-10, validación sincrónica: el campo se marca en rojo mientras no sea un número.
        txtValor.textProperty().addListener((obs, anterior, nuevo) -> marcarValido(txtValor, nuevo));
        txtMontoPago.textProperty().addListener((obs, anterior, nuevo) -> marcarValido(txtMontoPago, nuevo));
        // Enter en el valor envía el formulario (interacción con teclado).
        txtValor.setOnAction(e -> onAsignarObligacion());

        // Doble clic en una obligación: copia su saldo al campo de pago (interacción con mouse).
        tablaObligaciones.setRowFactory(tabla -> {
            TableRow<ObligacionFinanciera> fila = new TableRow<>();
            fila.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !fila.isEmpty()) {
                    txtMontoPago.setText(String.format(Locale.ROOT, "%.2f", fila.getItem().calcularValor()));
                }
            });
            return fila;
        });

        refrescar();
    }

    /** Vuelve a leer el gestor y actualiza las tablas, conservando la propiedad seleccionada. */
    public void refrescar() {
        Propiedad seleccionada = tablaPropiedades.getSelectionModel().getSelectedItem();
        tablaPropiedades.setItems(FXCollections.observableArrayList(gestor.getPropiedades()));
        if (seleccionada != null) {
            tablaPropiedades.getSelectionModel().select(seleccionada);
        }
        mostrarObligaciones(tablaPropiedades.getSelectionModel().getSelectedItem());
    }

    private void mostrarObligaciones(Propiedad propiedad) {
        if (propiedad == null) {
            lblPropiedadSeleccionada.setText("3. Obligaciones (seleccione una propiedad)");
            tablaObligaciones.setItems(FXCollections.observableArrayList());
            return;
        }
        List<ObligacionFinanciera> lista = chkSoloActivas.isSelected()
                ? gestor.consultarDeudasActivas(propiedad.getIdentificador())
                : propiedad.getHistorial();
        lblPropiedadSeleccionada.setText("3. Obligaciones de " + propiedad);
        tablaObligaciones.setItems(FXCollections.observableArrayList(lista));
    }

    @FXML
    private void onRegistrarPropiedad() {
        try {
            Residente residente = new Residente(txtNombreResidente.getText(), txtDocumento.getText());
            Propiedad propiedad = new Propiedad(txtIdentificador.getText(), txtDireccion.getText(), residente);
            gestor.registrarPropiedad(propiedad);
            limpiar(txtIdentificador, txtDireccion, txtNombreResidente, txtDocumento);
            lblMensaje.setText("Propiedad " + propiedad + " registrada.");
            alCambiarDatos.run();
        } catch (IllegalArgumentException | ReglaNegocioException e) {
            Dialogos.aviso(e.getMessage());
        }
    }

    @FXML
    private void onAsignarObligacion() {
        Propiedad propiedad = tablaPropiedades.getSelectionModel().getSelectedItem();
        if (propiedad == null) {
            Dialogos.aviso("Seleccione una propiedad en la tabla.");
            return;
        }
        if (dpVencimiento.getValue() == null) {
            Dialogos.aviso("Seleccione la fecha de vencimiento.");
            return;
        }
        Double valor = leerMonto(txtValor, "El valor");
        if (valor == null) {
            return;
        }
        try {
            ObligacionFinanciera obligacion = crearObligacion(
                    comboTipo.getValue(), txtDescripcion.getText(), valor, dpVencimiento.getValue());
            gestor.asignarObligacion(propiedad.getIdentificador(), obligacion);
            limpiar(txtDescripcion, txtValor);
            lblMensaje.setText(obligacion.getTipo() + " asignada a " + propiedad + ".");
            alCambiarDatos.run();
        } catch (IllegalArgumentException | ReglaNegocioException e) {
            Dialogos.aviso(e.getMessage());
        }
    }

    @FXML
    private void onRegistrarPago() {
        Propiedad propiedad = tablaPropiedades.getSelectionModel().getSelectedItem();
        ObligacionFinanciera obligacion = tablaObligaciones.getSelectionModel().getSelectedItem();
        if (propiedad == null || obligacion == null) {
            Dialogos.aviso("Seleccione una propiedad y una obligación en las tablas.");
            return;
        }
        Double monto = leerMonto(txtMontoPago, "El monto a pagar");
        if (monto == null) {
            return;
        }
        try {
            gestor.registrarPago(propiedad.getIdentificador(), obligacion, monto, comboMetodo.getValue());
            limpiar(txtMontoPago);
            lblMensaje.setText("Pago registrado. " + obligacion.getTipo() + " queda "
                    + obligacion.getEstado().getEtiqueta() + ".");
            alCambiarDatos.run();
        } catch (IllegalArgumentException | IllegalStateException | ReglaNegocioException e) {
            Dialogos.aviso(e.getMessage());
        }
    }

    /** RF-11: lista los pagos ya realizados de la propiedad seleccionada. */
    @FXML
    private void onVerHistorialPagos() {
        Propiedad propiedad = tablaPropiedades.getSelectionModel().getSelectedItem();
        if (propiedad == null) {
            Dialogos.aviso("Seleccione una propiedad en la tabla.");
            return;
        }
        List<ObligacionFinanciera> pagos = gestor.historialDePagos(propiedad.getIdentificador());
        if (pagos.isEmpty()) {
            Dialogos.info("Historial de pagos", "Esta propiedad todavía no tiene pagos registrados.");
            return;
        }
        String texto = pagos.stream()
                .map(p -> p.getFechaPago() + "  " + p.getTipo() + " - " + p.getDescripcion()
                        + "  " + formatoDinero(p.getValorBase()) + "  (" + p.getMetodoPago() + ")")
                .collect(Collectors.joining("\n"));
        Dialogos.info("Historial de pagos de " + propiedad, texto);
    }

    /** RF-13: muestra el comprobante del pago seleccionado en la tabla de obligaciones. */
    @FXML
    private void onVerComprobante() {
        Propiedad propiedad = tablaPropiedades.getSelectionModel().getSelectedItem();
        ObligacionFinanciera obligacion = tablaObligaciones.getSelectionModel().getSelectedItem();
        if (propiedad == null || obligacion == null) {
            Dialogos.aviso("Seleccione una propiedad y una obligación pagada en las tablas.");
            return;
        }
        try {
            Comprobante comprobante = gestor.generarComprobante(propiedad.getIdentificador(), obligacion);
            Dialogos.info("Comprobante de pago", comprobante.comoTexto());
        } catch (IllegalArgumentException | IllegalStateException e) {
            Dialogos.aviso(e.getMessage());
        }
    }

    @FXML
    private void onFiltrarObligaciones() {
        mostrarObligaciones(tablaPropiedades.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void onReporteMorosos() {
        List<Propiedad> morosas = gestor.generarReporteCarteraMorosa();
        if (morosas.isEmpty()) {
            Dialogos.info("Cartera morosa", "No hay propiedades con deudas vencidas.");
            return;
        }
        String texto = morosas.stream()
                .map(p -> p.getIdentificador() + " - " + p.getResidente().getNombre()
                        + ": " + formatoDinero(p.calcularDeudaTotal()))
                .collect(Collectors.joining("\n"));
        Dialogos.info("Cartera morosa", texto);
    }

    /** Fábrica de la interfaz: el combo elige el tipo y aquí se crea la subclase correspondiente. */
    private ObligacionFinanciera crearObligacion(String tipo, String descripcion, double valor, LocalDate vencimiento) {
        return switch (tipo) {
            case "Multa" -> new Multa(descripcion, valor, vencimiento);
            case "Reserva" -> new CargoReserva(descripcion, valor, vencimiento);
            default -> new CuotaMensual(descripcion, valor, vencimiento);
        };
    }

    /** RF-10: si el texto no es un número, avisa y no deja continuar. */
    private Double leerMonto(TextField campo, String nombreCampo) {
        try {
            return Double.parseDouble(campo.getText().trim());
        } catch (NumberFormatException e) {
            Dialogos.aviso(nombreCampo + " debe ser un número, por ejemplo 150000 o 1500.50.");
            return null;
        }
    }

    private void marcarValido(TextField campo, String texto) {
        boolean valido = texto == null || texto.isBlank() || esNumero(texto);
        campo.setStyle(valido ? "" : "-fx-border-color: #c0392b; -fx-border-width: 2;");
    }

    private static boolean esNumero(String texto) {
        try {
            Double.parseDouble(texto.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static String formatoDinero(double valor) {
        return String.format(Locale.ROOT, "$%,.2f", valor);
    }

    private void limpiar(TextField... campos) {
        for (TextField campo : campos) {
            campo.clear();
        }
    }
}
