import java.util.ArrayList;
import java.util.List;

public class AdministradorConjunto {
    private String nombre;
    private List<Propiedad> propiedades;
    private List<ZonaComun> zonasComunes;

    public AdministradorConjunto(String nombre) {
        this.nombre = nombre;
        this.propiedades = new ArrayList<>();
        this.zonasComunes = new ArrayList<>();
    }

    public void registrarPropiedad(Propiedad p) {
        if (buscarPropiedad(p.getIdentificador()) != null) {
            throw new IllegalArgumentException(
                    "Ya existe una propiedad con el identificador \"" + p.getIdentificador() + "\".");
        }
        propiedades.add(p);
    }

    public Propiedad buscarPropiedad(String identificador) {
        for (Propiedad p : propiedades) {
            if (p.getIdentificador().equalsIgnoreCase(identificador)) {
                return p;
            }
        }
        return null;
    }

    // RF-07
    public void configurarZonaComun(ZonaComun z) {
        zonasComunes.add(z);
    }

    public void registrarPago(ObligacionFinanciera o, double monto, MetodoPago metodo) {
        o.pagar(monto, metodo);
    }

    public List<Propiedad> generarReporteCarteraMorosa() {
        List<Propiedad> morosas = new ArrayList<>();
        for (Propiedad p : propiedades) {
            if (p.tieneDeudasEnMora()) {
                morosas.add(p);
            }
        }
        return morosas;
    }

    public String getNombre() {
        return nombre;
    }

    public List<Propiedad> getPropiedades() {
        return propiedades;
    }

    public List<ZonaComun> getZonasComunes() {
        return zonasComunes;
    }
}
