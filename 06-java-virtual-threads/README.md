# 🧵 Java Virtual Threads (Java 21)

---

## 🎯 Introducción

Los `Virtual Threads` son una de las mayores innovaciones introducidas en `Java 21` gracias a `Project Loom`.
Su objetivo es simplificar la programación concurrente y escalar aplicaciones con miles de tareas simultáneas,
sin la complejidad de manejar manualmente los `platform threads` tradicionales.

## 🔍 ¿Qué son los Virtual Threads?

Los `Virtual Threads` son hilos ligeros gestionados por la `JVM (Java Virtual Machine)` en lugar del sistema operativo.
Son instancias de `java.lang.Thread` que no están vinculadas `1:1` con los hilos del sistema operativo.

### ✨ Características Principales

- **🪶 Ligeros**: Consumen muy poca memoria (pocos KB vs ~1MB de platform threads).
- **⚡ Baratos de crear**: Se pueden crear millones sin degradar el rendimiento.
- **🔄 Transparentes**: Usan la misma API de Thread que conoces.
- **🎯 Optimizados para I/O**: Perfectos para operaciones de entrada/salida bloqueantes.

> 💡 En esencia, permiten escribir código bloqueante tradicional, pero con escalabilidad masiva.

### 💡 Concepto Clave

- `Platform Thread`: Wrapper de un OS Thread (pesado, limitado)
- `Virtual Thread`: Hilo gestionado por la JVM (ligero, escalable)

### 🔄 Diferencias con Platform Threads

| Característica        | Platform Threads (clásicos)                           | Virtual Threads (Java 21)                                     |
|-----------------------|-------------------------------------------------------|---------------------------------------------------------------|
| **Implementación**    | Se apoyan directamente en hilos del sistema operativo | Son hilos ligeros gestionados por la JVM                      |
| **Costo de creación** | Alto (recurso del SO)                                 | Muy bajo (miles pueden coexistir)                             |
| **Bloqueo I/O**       | Bloquear un hilo implica bloquear un recurso del SO   | El hilo virtual se "aparca" y libera el hilo físico           |
| **Escalabilidad**     | Limitada (decenas o cientos de hilos)                 | Masiva (miles o millones de hilos concurrentes)               |
| **Uso típico**        | Procesos pesados, cálculos intensivos                 | Servidores concurentes, microservicios, tareas I/O intensivas |

💡 Ejemplo laboral:

- `Platform threads`: procesamiento de imágenes pesadas en batch.
- `Virtual threads`: servidor HTTP que atiende miles de peticiones concurrentes de clientes.

