package conjunto.modelo;

public class Piscina extends ZonaComun {
    private final double profundidadMaxima;
    private final boolean requiereCertificadoSalud;

    public Piscina(String nombre, int capacidadMaxima, double costoAlquiler,
                   double profundidadMaxima, boolean requiereCertificadoSalud) {
        super(nombre, capacidadMaxima, costoAlquiler);
        this.profundidadMaxima = profundidadMaxima;
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
        return "Profundidad: " + profundidadMaxima + " m, certificado: " + requiereCertificadoSalud;
    }
}