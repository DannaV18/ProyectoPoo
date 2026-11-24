package conjunto.gui;

import javafx.scene.control.Alert;

/** Mensajes emergentes en un solo lugar: así todas las ventanas avisan igual. */
final class Dialogos {

    private Dialogos() {
        // Clase utilitaria: no se instancia.
    }

    static void aviso(String mensaje) {
        mostrar(Alert.AlertType.WARNING, "Revise el dato", mensaje);
    }

    static void error(String mensaje) {
        mostrar(Alert.AlertType.ERROR, "Error", mensaje);
    }

    static void info(String titulo, String mensaje) {
        mostrar(Alert.AlertType.INFORMATION, titulo, mensaje);
    }

    private static void mostrar(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
