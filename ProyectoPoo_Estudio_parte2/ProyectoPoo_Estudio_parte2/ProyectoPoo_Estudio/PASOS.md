# ProyectoPoo_Estudio — Parte 2 de 5

Pasos 3 a 4 del historial. El código de esta carpeta es el estado al cerrar el paso 4.

## Commits de esta parte

### Paso 3 — `983f643`

Paso 3: obligaciones financieras con herencia y polimorfismo

Pagable (interfaz), EstadoObligacion (enum), ObligacionFinanciera (abstracta)
y sus tipos CuotaMensual (5 % de mora), Multa (10 %) y CargoReserva (sin mora).
calcularValor() es un metodo plantilla: cada subclase aporta solo tasaMora().
Propiedad guarda el historial de obligaciones con lista privada.

### Paso 4 — `b9bfd2e`

Paso 4: zonas comunes y reservas en el modelo

ZonaComun (abstracta) con SalonSocial, ZonaBbq y CanchaDeFutbol, que
aportan su propio atributo mediante getDetalle(). Reserva valida el rango
de horas y expone seSolapaCon(), la regla que usa RF-09.

