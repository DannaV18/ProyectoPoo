Pubic class ZonaBbq extends ZonaComun{
    private int cantidadDeParrillas;

    public ZonaBbq(String nombre, int capacidadMaxima, double costoAlquiler,
                       int cantidadDeParrillas) {
        super(nombre, capacidadMaxima, costoAlquiler);
        this.cantidadDeParrillas = cantidadDeParrillas;
}
  
    @Override
    public String obtenerReglas() {
        return super.obtenerReglas() + " Depósito reembolsable de $" + valorDeposito + ".";
    }
}
