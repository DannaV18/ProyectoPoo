import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        System.out.println("Bienvenido al sistema de administración");
        System.out.println("Ingrese el nombre del residente:");
        Scanner scanner = new Scanner(System.in);
        String nombreResidente = scanner.nextLine();
        System.out.println("Bienvenido, " + nombreResidente + "!");

        // Crear un residente
        Residente residente = new Residente();
        residente.setNombre(nombreResidente);

        // Crear una propiedad para el residente
        System.out.println("Ingrese el identificador de la propiedad:");
        String identificadorPropiedad = scanner.nextLine();
        System.out.println("Ingrese la dirección de la propiedad:");
        String direccionPropiedad = scanner.nextLine();
        Propiedad propiedad = new Propiedad(identificadorPropiedad, direccionPropiedad, residente); 
        System.out.println("Propiedad registrada exitosamente para el residente " + residente.getNombre() + ".");
        
        propiedad.registrarObligacion(new Multa(200.0, java.time.LocalDate.of(2024, 11, 30), "Pendiente"));
    }
}

