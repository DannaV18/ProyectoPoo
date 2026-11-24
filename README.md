# Estudio: administración de un conjunto residencial


## Estructura por paquetes

| Paquete | Responsabilidad |
|---|---|
| `conjunto.modelo` | Entidades del dominio y sus reglas: encapsulamiento, herencia, polimorfismo e interfaces. |
| `conjunto.negocio` | Casos de uso. No sabe de archivos ni de ventanas. Es el único punto de entrada para la interfaz y la persistencia. |
| `conjunto.persistencia` | Lectura y escritura en archivos de texto. Depende de `negocio`, nunca al revés. |
| `conjunto.gui` | Ventanas JavaFX (FXML para Scene Builder) y controladores que solo validan datos y delegan. |

## Trazabilidad: requisitos → clases → temas del curso

| Requisito | Clases involucradas | Temas |
|---|---|---|
| RF-01 Registrar propiedad con residente, sin repetidos | `GestorConjunto.registrarPropiedad`, `Propiedad`, `Residente` | Clases, constructores, encapsulamiento |
| RF-02 Asignar cuota, multa o reserva | `ObligacionFinanciera` y subclases `CuotaMensual`, `Multa`, `CargoReserva` | Herencia, abstracción |
| RF-03 Consultar deudas activas | `Propiedad.consultarDeudasActivas`, `GestorConjunto.consultarDeudasActivas` | Colecciones, encapsulamiento |
| RF-04 Total con la regla de cada tipo | `ObligacionFinanciera.calcularValor` (plantilla) y `tasaMora()` en cada subclase | Polimorfismo, sobrescritura |
| RF-05 Registrar pago | `Pagable`, `ObligacionFinanciera.pagar` | Interfaces |
| RF-06 Reporte de morosos | `GestorConjunto.generarReporteCarteraMorosa` | Colecciones |
| RF-07 Zonas comunes | `ZonaComun` (abstracta), `SalonSocial`, `ZonaBbq`, `CanchaDeFutbol` | Herencia, clases abstractas |
| RF-08 Solicitar reserva (rechaza mora) | `GestorConjunto.solicitarReserva`, `Propiedad.tieneDeudasEnMora` | Relaciones entre clases |
| RF-09 Verificar disponibilidad | `Reserva.seSolapaCon`, `GestorConjunto.reservasDe` | Lógica en el modelo |
| RF-10 Validar datos de la interfaz | `PropiedadesController.leerMonto` y `marcarValido` | Validación de datos, eventos |
| RNF-01 Guardar en archivos | `RepositorioArchivo.guardar` y `cargar` | Archivos planos, CRUD |
| RNF-02 Manejar errores de archivos | `RepositorioArchivo.cargar`, `PersistenciaException` | Excepciones chequeadas |
| RF-11 Historial de pagos del residente | `GestorConjunto.historialDePagos` | Colecciones, filtrado |
| RF-12 Cancelar una reserva propia | `GestorConjunto.cancelarReserva`, `Reserva.cancelar` | Relaciones, reglas en el modelo |
| RF-13 Comprobante de pago | `Comprobante.de`, `GestorConjunto.generarComprobante` | Clases inmutables, factory method |

## Decisiones de POO (resumen)

1. **`ObligacionFinanciera` es abstracta y `calcularValor()` es un método plantilla.** Cada tipo solo aporta su tasa de mora. Así el código de afuera no pregunta de qué tipo es.
2. **`ZonaComun` es abstracta.** Las subclases tienen atributos distintos, pero el nombre, la capacidad y el costo viven una sola vez.
3. **`ReglaNegocioException` separa dos fallos.** `IllegalArgumentException` significa dato mal formado; `ReglaNegocioException` significa que el dato es válido pero la regla no lo permite.
4. **La persistencia usa un archivo temporal.** Escribir en `.tmp` y luego reemplazar evita dejar el archivo a medias si algo falla.
5. **El tipo de obligación se guarda con un código que declara cada subclase** (`getCodigoArchivo()`), en vez de comparar textos con `if` o `instanceof`.

## Cómo ejecutar

**Pruebas de consola:**

```
javac -d target/classes $(find src/main/java -name "*.java" ! -path "*/gui/*")
javac -cp target/classes -d target/test-classes src/test/java/conjunto/Verificacion.java
java -cp target/classes:target/test-classes conjunto.Verificacion
```

Resultado esperado: `15 de 15 comprobaciones correctas.`

**Interfaz gráfica:** con Maven y Java 21, desde la raíz del proyecto:

```
mvn javafx:run
```

Abrir las vistas en Scene Builder: `src/main/resources/conjunto/gui/principal.fxml`, `propiedades.fxml` y `zonas_reservas.fxml`.

## Lo que todavía no está probado

La interfaz gráfica no se pudo compilar en el entorno donde se escribió porque no tenía JavaFX. Se verificó que los FXML son XML válido y que cada `fx:id` y `onAction` existe en su controlador, pero la primera ejecución real debe hacerse con `mvn javafx:run`. Si aparece un error, es probable que sea un nombre de método o de tipo que hay que ajustar.

## Limitaciones conocidas

- Al cancelar una reserva con costo, su cargo sigue pendiente en el historial de la propiedad. Revertirlo requeriría vincular el cargo con la reserva y guardar esa relación en el archivo.
- No hay usuarios ni contraseñas: la ventana permite elegir qué propiedad actúa. Esa elección es de la interfaz, y la regla de quién puede cancelar sí se valida en negocio.
