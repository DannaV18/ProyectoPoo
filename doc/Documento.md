# Bitácora de Desarrollo y Evolución del Proyecto — Conjunto Residencial

Este documento registra el procedimiento cronológico de análisis, diseño, pruebas y refactorización del sistema de administración del conjunto residencial, detallando tanto los avances implementados como los ajustes realizados al detectar limitaciones técnicas durante la programación.

---

### 1. Definición inicial del dominio y alcance (20/09/2026)
* **Qué se hizo:** Se definió el contexto del problema alrededor de la administración de un conjunto residencial de casas y el control de sus zonas comunes, estableciendo los actores principales (**Administrador** y **Residente**) y los requerimientos funcionales iniciales (`RF-01` a `RF-10`) junto con la persistencia básica (`RNF-01`, `RNF-02`).
* **Ajuste realizado:** Inicialmente se consideró modelar lotes vacíos como un tipo especial de propiedad, pero al revisar el alcance se vio que eso complicaba innecesariamente la jerarquía de `Propiedad` sin aportar lógica relevante. Se descartó para centrar la herencia y el polimorfismo en los tipos de cobro.

### 2. Primer diseño orientado a objetos y diagrama UML inicial (20/10/2026)
* **Qué se hizo:** Se construyó el primer diagrama de clases para la Entrega 2, estructurando las entidades `Propiedad`, `Residente`, `AdministradorConjunto`, `ZonaComun`, `Reserva` (con su clase anidada `Horario`), las interfaces `Pagable` y `Reservable`, y la jerarquía de `ObligacionFinanciera` (`CuotaMensual`, `Multa` y `Reserva`).

![Diagrama UML Inicial - 20/10/2026](ruta/a/diagrama_uml_inicial.png)

* **Problemas detectados al intentar pasarlo a código:**
  * **Fallo conceptual en `Reserva`:** En este diagrama `Reserva` heredaba directamente de `ObligacionFinanciera`. Al empezar a programar los casos de uso nos dimos cuenta de que **esto no podía funcionar así**: si un residente reservaba una zona gratuita (como la cancha), el sistema lo obligaba a crear una deuda financiera de $0, y cancelar una reserva implicaba tener que borrar o alterar registros del historial financiero de la propiedad.
  * **Falta de contexto en `Reservable`:** Se intentó implementar `Reservable` dentro de `ZonaComun` (`estaDisponible(fecha, horario)`), pero al escribir el método se vio que una zona por sí sola no conoce la lista global de reservas del conjunto, por lo que no podía validar cruces de horario sin acoplarse a todo el sistema.
  * **Clase anidada `Horario` innecesaria:** Mantener una clase interna `Horario` solo para guardar hora de inicio y fin agregaba código extra cuando `java.time.LocalTime` ya resolvía las comparaciones de intervalos de forma nativa.

### 3. Reestructuración de arquitectura por capas y nacimiento de `GestorConjunto` (02/11/2026)
* **Qué se hizo:** Se dividió el proyecto en paquetes separados (`conjunto.modelo`, `conjunto.negocio`, `conjunto.persistencia` y `conjunto.gui`) para evitar mezclar lógica de negocio con ventanas o archivos.
* **Ajuste realizado:** En el diagrama del 20 de octubre existía la clase `AdministradorConjunto` dentro del modelo. Al implementar las reglas que cruzan varias entidades (como evitar propiedades duplicadas o bloquear reservas a morosos), vimos que `AdministradorConjunto` terminaba duplicando responsabilidades con la capa de negocio y quedaba sin uso real. Se decidió **eliminarla** y centralizar todos los casos de uso en `GestorConjunto` (en `conjunto.negocio`), apoyado por la excepción propia `ReglaNegocioException`.

### 4. Separación entre `Reserva` y `CargoReserva`, y especialización de zonas (08/11/2026)
* **Qué se hizo:** Se dividió el concepto de reserva en dos clases independientes:
  1. `Reserva`: gestiona exclusivamente la propiedad solicitante, la zona, la fecha, `horaInicio`, `horaFin` (`LocalTime`) y su estado (`ACTIVA` o `CANCELADA`), incluyendo el método `seSolapaCon()` para detectar cruces.
  2. `CargoReserva`: subclase de `ObligacionFinanciera` que únicamente se instancia en `GestorConjunto.solicitarReserva()` cuando la zona tiene un costo de alquiler mayor a cero, asignándole un plazo de pago de 15 días (`DIAS_PLAZO_RESERVA`).
* **Ajuste realizado:** También se notó que `ZonaComun` como clase concreta no permitía diferenciar las características propias de cada espacio. Se convirtió en **clase abstracta** y se crearon sus cuatro especializaciones (`SalonSocial`, `ZonaBbq`, `CanchaDeFutbol` y `Piscina`), delegando en `getDetalle()` el formato específico de cada una para guardarlo en texto plano.

### 5. Refactorización del polimorfismo y reemplazo de estáticos (13/11/2026)
* **Qué se hizo:** Se simplificó el cálculo de deudas y se limpiaron los atributos estáticos que venían del diseño de octubre.
* **Ajuste realizado:**
  * En el diseño anterior cada subclase sobrescribía `calcularValor()`, repitiendo exactamente la misma fórmula matemática. Se cambió por un método plantilla en `ObligacionFinanciera` y se dejó como abstracto únicamente `tasaMora()`, donde `CuotaMensual` aporta `5 %`, `Multa` `10 %` y `CargoReserva` `0 %`.
  * Los estáticos `valorCuotaBase`, `VALOR_BASE_MULTA` y `totalPropiedades` se descartaron porque en la práctica cada cobro tiene un monto variable ingresado desde la interfaz y el total se obtiene directamente del tamaño de la lista en `GestorConjunto`. En su lugar se dejaron constantes estáticas reales (`RECARGO_MORA`, `DIAS_PLAZO_RESERVA`) y métodos de fábrica/utilidad (`Comprobante.de()`, `Piscina.desdeDetalle()`, `Validar`).
  * Se reemplazaron los estados manejados como `String` (propensos a errores de escritura) por las enumeraciones `EstadoObligacion`, `EstadoReserva` y `MetodoPago`.

### 6. Implementación de persistencia atómica, comprobantes y pruebas de borde (18/11/2026)
* **Qué se hizo:** Se construyó `RepositorioArchivo` para guardar y cargar el estado completo en `datos_conjunto.txt`, se agregaron los requerimientos `RF-11` (historial de pagos), `RF-12` (cancelación de reservas) y `RF-13` (clase inmutable `Comprobante`), y se creó la suite `Verificacion.java` con 19 pruebas unitarias y de integración.
* **Errores encontrados durante las pruebas y cómo se corrigieron:**
  * **Error de precisión en punto flotante:** Al probar una multa de `$100.000` en mora (`× 1.10`), Java producía `110000.00000000001`, haciendo que un pago exacto de `$110.000` fallara por decimales residuales. Se corrigió redondeando `calcularValor()` a dos decimales (`Math.round(valor * 100) / 100.0`).
  * **Validación de `NaN` e `Infinity`:** Las validaciones iniciales (`valor <= 0`) dejaban pasar `Double.NaN` porque cualquier comparación con `NaN` evalúa a `false`. Se creó la clase `Validar` usando `Double.isFinite()` y bloqueando el carácter `;` para no corromper el separador del archivo plano.
  * **Escritura segura:** Para evitar perder datos si ocurría un fallo al guardar, se modificó `RepositorioArchivo.guardar()` para escribir primero en `datos_conjunto.txt.tmp` y luego reemplazar el archivo principal.
  * **Inconsistencia en comprobantes con mora:** Al pagar una obligación vencida, el comprobante mostraba inicialmente solo el valor base en lugar del total cobrado con recargo; se añadió el atributo `valorPagado` a `ObligacionFinanciera` y se actualizó el formato del archivo manteniendo compatibilidad con registros anteriores.

### 7. Interfaz gráfica en JavaFX y actualización del diagrama UML final (22/11/2026)
* **Qué se hizo:** Se completaron las vistas FXML (`principal.fxml`, `propiedades.fxml`, `zonas_reservas.fxml`) y sus controladores (`PrincipalController`, `PropiedadesController`, `ZonasReservasController`), conectando validaciones visuales en tiempo real (campos marcados en rojo ante datos inválidos, `RF-10`) y guardado automático tras cada operación. Finalmente, se generó el diagrama UML definitivo reflejando fielmente la estructura de clases, paquetes y relaciones del código en `main`.

![Diagrama UML Final - 22/11/2026](ruta/a/diagrama_uml_actualizado.png)
