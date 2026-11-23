package conjunto.modelo;

/**
 * Espacio compartido del conjunto (salón, cancha, zona BBQ...). Es abstracta porque
 * cada tipo tiene atributos propios; lo que todas comparten vive aquí.
 */
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

    /** Tipo de zona para guardar y mostrar: "SALON", "BBQ" o "CANCHA". */
    public abstract String getTipo();

    /** Atributo propio del tipo, en texto, para guardar en archivo (por ejemplo "true" o "3"). */
    public abstract String getDetalle();

    /** Si la zona tiene costo de alquiler (RF-07). */
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
