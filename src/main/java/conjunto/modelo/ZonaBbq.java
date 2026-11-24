
package conjunto.modelo;

/** Zona de asados con cantidad de parrillas. */
public class ZonaBbq extends ZonaComun {

    private final int cantidadParrillas;

    public ZonaBbq(String nombre, int capacidadMaxima, double costoAlquiler, int cantidadParrillas) {
        super(nombre, capacidadMaxima, costoAlquiler);
        if (cantidadParrillas <= 0) {
            throw new IllegalArgumentException("Debe tener al menos una parrilla.");
        }
        this.cantidadParrillas = cantidadParrillas;
    }

    public int getCantidadParrillas() {
        return cantidadParrillas;
    }

    @Override
    public String getTipo() {
        return "BBQ";
    }

    @Override
    public String getDetalle() {
        return String.valueOf(cantidadParrillas);
    }
}
