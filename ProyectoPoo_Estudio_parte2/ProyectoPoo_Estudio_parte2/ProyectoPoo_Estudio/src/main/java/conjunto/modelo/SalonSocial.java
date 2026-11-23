package conjunto.modelo;

/** Salón social. Puede tener cocina. */
public class SalonSocial extends ZonaComun {

    private final boolean tieneCocina;

    public SalonSocial(String nombre, int capacidadMaxima, double costoAlquiler, boolean tieneCocina) {
        super(nombre, capacidadMaxima, costoAlquiler);
        this.tieneCocina = tieneCocina;
    }

    public boolean isTieneCocina() {
        return tieneCocina;
    }

    @Override
    public String getTipo() {
        return "SALON";
    }

    @Override
    public String getDetalle() {
        return String.valueOf(tieneCocina);
    }
}
