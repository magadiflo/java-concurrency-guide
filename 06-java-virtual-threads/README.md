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

| Característica          | Platform Threads (clásicos)                           | Virtual Threads (Java 21)                                     |
|-------------------------|-------------------------------------------------------|---------------------------------------------------------------|
| **Implementación**      | Se apoyan directamente en hilos del sistema operativo | Son hilos ligeros gestionados por la JVM                      |
| **Bloqueo I/O**         | Bloquear un hilo implica bloquear un recurso del SO   | El hilo virtual se "aparca" y libera el hilo físico           |
| **Escalabilidad**       | Limitada (decenas o cientos de hilos)                 | Masiva (miles o millones de hilos concurrentes)               |
| **Uso típico**          | Procesos pesados, cálculos intensivos                 | Servidores concurentes, microservicios, tareas I/O intensivas |
| **🏗️ Gestión**         | Sistema Operativo                                     | JVM (Scheduler interno)                                       |
| **💾 Memoria**          | ~1 MB por thread                                      | ~1 KB por thread                                              |
| **📊 Cantidad máxima**  | Miles (~1000-5000)                                    | Millones (1M+)                                                |
| **⚡ Costo de creación** | Alto (system call)                                    | Muy bajo (objeto Java)                                        |
| **🔄 Context Switch**   | Costoso (kernel space)                                | Económico (user space)                                        |
| **🎯 Mejor uso**        | CPU-intensive                                         | I/O-intensive                                                 |
| **📌 Thread Pinning**   | No aplica                                             | Puede ocurrir con synchronized                                |
| **🛠️ Pool necesario**  | Sí (ExecutorService)                                  | No recomendado                                                |

💡 Ejemplo laboral:

- `Platform threads`: procesamiento de imágenes pesadas en batch.
- `Virtual threads`: servidor HTTP que atiende miles de peticiones concurrentes de clientes.

## 🚀 Project Loom

**Project Loom** es la iniciativa de OpenJDK que introduce los Virtual Threads en Java. Su objetivo principal es
simplificar la programación concurrente manteniendo el modelo tradicional de threads.

### 🎯 Objetivos de Project Loom

1. **Mejorar el throughput**: Permitir mayor concurrencia sin aumentar complejidad.
2. **Simplificar el código**: Mantener el estilo imperativo (evitar callbacks y reactive).
3. **Compatibilidad**: Funcionar con código existente sin cambios.
4. **Observabilidad**: Mantener las herramientas de debugging y profiling.

### 📊 Evolución

```
Java 19 (Sep 2022) → Preview Feature
Java 20 (Mar 2023) → Second Preview
Java 21 (Sep 2023) → Feature Estable ✅
```

## ⚖️ Diferencias con Platform Threads


