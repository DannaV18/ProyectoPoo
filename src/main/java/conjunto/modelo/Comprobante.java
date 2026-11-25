package conjunto.modelo;

import java.time.LocalDate;

public final class Comprobante {

    private final String numero;
    private final String identificadorPropiedad;
    private final String descripcion;
    private final double valorBase;
    private final double monto;
    private final LocalDate fechaPago;
    private final MetodoPago metodo;

    private Comprobante(String numero, String identificadorPropiedad, String descripcion,
                        double valorBase, double monto, LocalDate fechaPago, MetodoPago metodo) {
        this.numero = numero;
        this.identificadorPropiedad = identificadorPropiedad;
        this.descripcion = descripcion;
        this.valorBase = valorBase;
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.metodo = metodo;
    }

    public static Comprobante de(Propiedad propiedad, ObligacionFinanciera obligacion) {
        int posicion = propiedad.getHistorial().indexOf(obligacion);
        if (posicion < 0) {
            throw new IllegalArgumentException("La obligación no pertenece a la propiedad " + propiedad + ".");
        }
        if (!obligacion.estaPagada()) {
            throw new IllegalStateException("Solo se genera comprobante de pagos realizados.");
        }
        String numero = String.format("CMP-%s-%03d",
                propiedad.getIdentificador().replace(" ", "").toUpperCase(), posicion + 1);
        return new Comprobante(numero, propiedad.getIdentificador(), obligacion.getDescripcion(),
                obligacion.getValorBase(), obligacion.getValorPagado(),
                obligacion.getFechaPago(), obligacion.getMetodoPago());
    }

    public String comoTexto() {
        return "COMPROBANTE DE PAGO\n"
                + "Número:     " + numero + "\n"
                + "Propiedad:  " + identificadorPropiedad + "\n"
                + "Concepto:   " + descripcion + "\n"
                + "Valor base: $" + String.format("%,.2f", valorBase) + "\n"
                + "Recargo:    $" + String.format("%,.2f", monto - valorBase) + "\n"
                + "Pagado:     $" + String.format("%,.2f", monto) + "\n"
                + "Fecha:      " + fechaPago + "\n"
                + "Método:     " + metodo;
    }

    public String getNumero() {
        return numero;
    }

    public double getValorBase() {
        return valorBase;
    }

    public double getMonto() {
        return monto;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public MetodoPago getMetodo() {
        return metodo;
    }
}
