
public class ZonaComun {
    public String nombre;
    protected int CapacidadMax;
    protected double CostoAlquiler;

    public ZonaComun(String nombre, int CapacidadMax, double CostoAlquiler) {
        this.nombre = nombre;
        this.CapacidadMax = CapacidadMax;
        this.CostoAlquiler = CostoAlquiler;
    }

    public String getNombre() {
        return nombre;
    }

    public int GetCapacidadMax() {
        return CapacidadMax;
    }

    public double getCostoAlquiler() {
        return CostoAlquiler;
    }
}
