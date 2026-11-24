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

/** Configura la persistencia, el gestor y la ventana principal de JavaFX. */
public class AplicacionConjunto extends Application {

    private static final Path ARCHIVO_DATOS = Path.of("datos_conjunto.txt");

    public static void iniciar(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage ventana) throws Exception {
        RepositorioArchivo repositorio = new RepositorioArchivo(ARCHIVO_DATOS);
        GestorConjunto gestor;
        try {
            gestor = repositorio.cargar();
        } catch (PersistenciaException e) {
            Dialogos.error(e.getMessage() + "\nCorrija el archivo y vuelva a abrir el programa.");
            Platform.exit();
            return;
        }

        FXMLLoader cargador = new FXMLLoader(AplicacionConjunto.class.getResource("principal.fxml"));
        Parent raiz = cargador.load();
        PrincipalController controlador = cargador.getController();
        controlador.inicializar(gestor, repositorio);

        ventana.setTitle("Administración del conjunto (estudio)");
        ventana.setScene(new Scene(raiz, 1100, 700));
        ventana.show();
    }
}