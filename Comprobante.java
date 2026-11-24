package conjunto.modelo;

import java.time.LocalDate;

/**
 * Comprobante de un pago realizado (RF-13).
 *
 * <p>No se guarda en un archivo aparte: se calcula a partir de la obligación pagada y su posición
 * en el historial de la propiedad. Como el historial sí se guarda, el comprobante se puede consultar
 * después de reiniciar el programa, y el mismo pago siempre produce el mismo número.</p>
 */
public final class Comprobante {

    private final String numero;
    private final String identificadorPropiedad;
    private final String descripcion;
    private final double monto;
    private final LocalDate fechaPago;
    private final MetodoPago metodo;

    private Comprobante(String numero, String identificadorPropiedad, String descripcion,
                        double monto, LocalDate fechaPago, MetodoPago metodo) {
        this.numero = numero;
        this.identificadorPropiedad = identificadorPropiedad;
        this.descripcion = descripcion;
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.metodo = metodo;
    }

    /** Solo se puede generar para una obligación ya pagada y que pertenezca a la propiedad. */
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
                obligacion.getValorBase(), obligacion.getFechaPago(), obligacion.getMetodoPago());
    }

    public String comoTexto() {
        return "COMPROBANTE DE PAGO\n"
                + "Número:     " + numero + "\n"
                + "Propiedad:  " + identificadorPropiedad + "\n"
                + "Concepto:   " + descripcion + "\n"
                + "Valor base: $" + String.format("%,.2f", monto) + "\n"
                + "Fecha:      " + fechaPago + "\n"
                + "Método:     " + metodo;
    }

    public String getNumero() {
        return numero;
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
