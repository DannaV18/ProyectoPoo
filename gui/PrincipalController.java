package conjunto.gui;

import conjunto.negocio.GestorConjunto;
import conjunto.persistencia.PersistenciaException;
import conjunto.persistencia.RepositorioArchivo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TabPane;

import java.io.IOException;

/**
 * Controlador de la ventana principal. Solo arma las pestañas y coordina el guardado:
 * cada vez que un controlador hija cambia algo, se guarda y se refrescan las demás vistas.
 */
public class PrincipalController {

    @FXML
    private TabPane tabs;

    private GestorConjunto gestor;
    private RepositorioArchivo repositorio;
    private PropiedadesController propiedades;
    private ZonasReservasController zonasReservas;

    public void inicializar(GestorConjunto gestor, RepositorioArchivo repositorio) throws IOException {
        this.gestor = gestor;
        this.repositorio = repositorio;

        FXMLLoader cargaPropiedades = new FXMLLoader(getClass().getResource("propiedades.fxml"));
        Parent vistaPropiedades = cargaPropiedades.load();
        propiedades = cargaPropiedades.getController();
        propiedades.inicializar(gestor, this::alCambiarDatos);

        FXMLLoader cargaZonas = new FXMLLoader(getClass().getResource("zonas_reservas.fxml"));
        Parent vistaZonas = cargaZonas.load();
        zonasReservas = cargaZonas.getController();
        zonasReservas.inicializar(gestor, this::alCambiarDatos);

        tabs.getTabs().get(0).setContent(vistaPropiedades);
        tabs.getTabs().get(1).setContent(vistaZonas);
    }

    private void alCambiarDatos() {
        try {
            repositorio.guardar(gestor);
        } catch (PersistenciaException e) {
            Dialogos.error(e.getMessage());
        }
        propiedades.refrescar();
        zonasReservas.refrescar();
    }
}
