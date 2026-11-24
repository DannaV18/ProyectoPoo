
package conjunto.modelo;

/**
 * Persona responsable de una propiedad. El documento es la identidad y no cambia,
 * por eso no tiene setter.
 */
public class Residente {

    private String nombre;
    private final String documento;

    public Residente(String nombre, String documento) {
        this.nombre = Validar.texto(nombre, "El nombre del residente es obligatorio.");
        this.documento = Validar.texto(documento, "El documento del residente es obligatorio.");
    }

    public String getNombre() {
        return nombre;
    }

    public void cambiarNombre(String nuevoNombre) {
        this.nombre = Validar.texto(nuevoNombre, "El nombre del residente es obligatorio.");
    }

    public String getDocumento() {
        return documento;
    }

    @Override
    public String toString() {
        return nombre + " (" + documento + ")";
    }
}
