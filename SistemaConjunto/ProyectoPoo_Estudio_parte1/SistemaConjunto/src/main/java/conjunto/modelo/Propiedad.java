package conjunto.modelo;

/**
 * Casa o apartamento del conjunto. Su identificador (por ejemplo "Casa 4") es único
 * dentro del conjunto; la unicidad se controla en la capa de negocio.
 */
public class Propiedad {

    private final String identificador;
    private final String direccion;
    private final Residente residente;

    public Propiedad(String identificador, String direccion, Residente residente) {
        this.identificador = Validar.texto(identificador, "El identificador de la propiedad es obligatorio.");
        this.direccion = Validar.texto(direccion, "La dirección es obligatoria.");
        if (residente == null) {
            throw new IllegalArgumentException("Toda propiedad necesita un residente responsable.");
        }
        this.residente = residente;
    }

    public String getIdentificador() {
        return identificador;
    }

    public String getDireccion() {
        return direccion;
    }

    public Residente getResidente() {
        return residente;
    }

    @Override
    public String toString() {
        return identificador;
    }
}
