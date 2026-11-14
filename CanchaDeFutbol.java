Pubic class CanchaDeFutbol extends ZonaComun{
    private String tipoDeSuperficie;
    private boolean tieneIluminacion;

    public CanchaDeFutbol(String nombre, int capacidadMaxima, double costoAlquiler,
                       String tipoDeSuperficie, boolean tieneIluminacion) {
        super(nombre, capacidadMaxima, costoAlquiler);
        this.tipoDeSuperficie = tipoDeSuperficie;
        this.tieneIluminacion = tieneIluminacion;
}
  
    @Override
    public String obtenerReglas() {
        return super.obtenerReglas() + " Depósito reembolsable de $" + valorDeposito + ".";
    }
}
