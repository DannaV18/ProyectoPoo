package conjunto.modelo;

public abstract class ZonaComun {

    private final String nombre;
    private final int capacidadMaxima;
    private final double costoAlquiler;

    protected ZonaComun(String nombre, int capacidadMaxima, double costoAlquiler) {
        this.nombre = Validar.texto(nombre, "El nombre de la zona es obligatorio.");
        if (capacidadMaxima <= 0) {
            throw new IllegalArgumentException("La capacidad máxima debe ser mayor a cero.");
        }
        this.capacidadMaxima = capacidadMaxima;
        this.costoAlquiler = Validar.noNegativo(costoAlquiler, "El costo de alquiler no puede ser negativo.");
    }

    public abstract String getTipo();

    public abstract String getDetalle();

    public boolean tieneCosto() {
        return costoAlquiler > 0;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public double getCostoAlquiler() {
        return costoAlquiler;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
