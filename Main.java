import java.time.LocalDate;
public class Main {
    public static void main(String[] args) {
        AdministradorConjunto admin = new AdministradorConjunto("Administrador");

        Residente residente = new Residente("Residente de prueba", "123456");
        Propiedad casa4 = new Propiedad("Casa 4", "Calle 1 # 2-3", residente);
        admin.registrarPropiedad(casa4);

        try {
            admin.registrarPropiedad(new Propiedad("Casa 4", "Otra dirección", residente));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        CuotaMensual cuota = new CuotaMensual(200000, LocalDate.now().minusDays(10), ObligacionFinanciera.PENDIENTE);
        Multa multa = new Multa(50000, LocalDate.now().plusDays(15), ObligacionFinanciera.PENDIENTE);
        casa4.registrarObligacion(cuota);
        casa4.registrarObligacion(multa);

        System.out.println("\nDeudas activas de " + casa4.getIdentificador() + ":");
        for (ObligacionFinanciera ob : casa4.consultarDeudas()) {
            System.out.println(" - " + ob.getClass().getSimpleName() + " | " + ob.getEstado()
                    + " | $" + ob.calcularValor());
        }
        System.out.println("Deuda total: $" + casa4.calcularDeudaTotal());
        System.out.println("Propiedades en cartera morosa: " + admin.generarReporteCarteraMorosa().size());

        admin.registrarPago(cuota, cuota.calcularValor(), MetodoPago.PSE);
        System.out.println("\nTras pagar la cuota -> estado: " + cuota.getEstado());
        System.out.println("Deuda total: $" + casa4.calcularDeudaTotal());
        System.out.println("Propiedades en cartera morosa: " + admin.generarReporteCarteraMorosa().size());
    }
}
