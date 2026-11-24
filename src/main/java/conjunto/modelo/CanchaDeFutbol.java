
package conjunto.modelo;

/** Cancha deportiva. Puede tener iluminación para jugar de noche. */
public class CanchaDeFutbol extends ZonaComun {

    private final boolean tieneIluminacion;

    public CanchaDeFutbol(String nombre, int capacidadMaxima, double costoAlquiler, boolean tieneIluminacion) {
        super(nombre, capacidadMaxima, costoAlquiler);
        this.tieneIluminacion = tieneIluminacion;
    }

    public boolean isTieneIluminacion() {
        return tieneIluminacion;
    }

    @Override
    public String getTipo() {
        return "CANCHA";
    }

    @Override
    public String getDetalle() {
        return String.valueOf(tieneIluminacion);
    }
}
