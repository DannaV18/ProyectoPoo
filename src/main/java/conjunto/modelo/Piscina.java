package conjunto.modelo;

public class Piscina extends ZonaComun {

    private final double profundidadMaxima;
    private final boolean requiereCertificadoSalud;

    public Piscina(String nombre, int capacidadMaxima, double costoAlquiler,
                   double profundidadMaxima, boolean requiereCertificadoSalud) {
        super(nombre, capacidadMaxima, costoAlquiler);
        this.profundidadMaxima = Validar.positivo(profundidadMaxima, "La profundidad debe ser mayor a cero.");
        this.requiereCertificadoSalud = requiereCertificadoSalud;
    }

    public double getProfundidadMaxima() {
        return profundidadMaxima;
    }

    public boolean isRequiereCertificadoSalud() {
        return requiereCertificadoSalud;
    }

    @Override
    public String getTipo() {
        return "PISCINA";
    }

    @Override
    public String getDetalle() {
        return profundidadMaxima + "," + requiereCertificadoSalud;
    }

    public static Piscina desdeDetalle(String nombre, int capacidad, double costo, String detalle) {
        String[] partes = detalle.split(",", -1);
        if (partes.length != 2) {
            throw new IllegalArgumentException("Detalle de piscina inválido: " + detalle);
        }
        return new Piscina(nombre, capacidad, costo, Double.parseDouble(partes[0]), Boolean.parseBoolean(partes[1]));
    }
}
