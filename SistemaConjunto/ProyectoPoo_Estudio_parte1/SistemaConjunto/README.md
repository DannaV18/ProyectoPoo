# Estudio: administración de un conjunto residencial

Proyecto de **estudio** (no es el entregable de un equipo). Sirve para repasar POO
construyendo un sistema completo paso a paso. Cada commit es un paso numerado.

## Estructura por paquetes

| Paquete | Responsabilidad |
|---|---|
| `conjunto.modelo` | Entidades del dominio y sus reglas (encapsulamiento, herencia, polimorfismo). |
| `conjunto.negocio` | Casos de uso del administrador y del residente. No sabe nada de archivos ni de la interfaz. |
| `conjunto.persistencia` | Lectura y escritura en archivos de texto. Depende de `negocio`, no al revés. |
| `conjunto.gui` | Ventanas JavaFX (FXML para Scene Builder) y controladores que solo delegan en `negocio`. |

## Requisitos implementados

Los requisitos RF-01 a RF-10 y RNF-01, RNF-02 del documento de la Entrega 1.

## Cómo ejecutar

Con Maven y Java 21: `mvn javafx:run`. Las pruebas de consola: `Verificacion` en `src/test/java`.
