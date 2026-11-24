package conjunto.gui;

import conjunto.negocio.GestorConjunto;
import conjunto.persistencia.PersistenciaException;
import conjunto.persistencia.RepositorioArchivo;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Path;

/** Punto de entrada. Carga los datos y muestra la ventana principal definida en principal.fxml. */
public class Main extends Application {

    private static final Path ARCHIVO_DATOS = Path.of("datos_conjunto.txt");

    @Override
    public void start(Stage ventana) throws Exception {
        RepositorioArchivo repositorio = new RepositorioArchivo(ARCHIVO_DATOS);
        GestorConjunto gestor;
        try {
            gestor = repositorio.cargar();
        } catch (PersistenciaException e) {
            // Si el archivo está dañado no se abre el programa: así no se sobrescriben datos que aún se pueden rescatar.
            Dialogos.error(e.getMessage() + "\nCorrija el archivo y vuelva a abrir el programa.");
            Platform.exit();
            return;
        }

        FXMLLoader cargador = new FXMLLoader(Main.class.getResource("principal.fxml"));
        Parent raiz = cargador.load();
        PrincipalController controlador = cargador.getController();
        controlador.inicializar(gestor, repositorio);

        ventana.setTitle("Administración del conjunto (estudio)");
        ventana.setScene(new Scene(raiz, 1100, 700));
        ventana.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
