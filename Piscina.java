Pubic class Piscina extends ZonaComun{
    private double profundidadMaxima;
    private boolean requiereCertificadoSalud;

    public CanchaDeFutbol(String nombre, int capacidadMaxima, double costoAlquiler,
                       double profundidadMaxima, boolean requiereCertificadoSalud) {
        super(nombre, capacidadMaxima, costoAlquiler);
        this.profundidadMaxima = profundidadMaxima;
        this.requiereCertificadoSalud = requiereCertificadoSalud;
}
  
    @Override
    public String obtenerReglas() {
        return super.obtenerReglas() + " Depósito reembolsable de $" + valorDeposito + ".";
    }
}
