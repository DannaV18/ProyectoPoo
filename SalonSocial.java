public class SalonSocial extends ZonaComun {
    private final boolean tieneCocina;
    private final double valorDeposito;

    public SalonSocial(String nombre, int capacidadMaxima, double costoAlquiler,
                       boolean tieneCocina, double valorDeposito) {
        super(nombre, capacidadMaxima, costoAlquiler);
        this.tieneCocina = tieneCocina;
        this.valorDeposito = valorDeposito;
    }

    @Override
    public String obtenerReglas() {
        return super.obtenerReglas() + " Depósito reembolsable de $" + valorDeposito + "."
                + (tieneCocina ? " Incluye uso de cocina." : "");
    }

    @Override
    public String getTipo() { return "SALON"; }

    @Override
    public java.util.List<String> getExtras() {
        return java.util.List.of(String.valueOf(tieneCocina), String.valueOf(valorDeposito));
    }

    public boolean isTieneCocina() { return tieneCocina; }
    public double getValorDeposito() { return valorDeposito; }
}
