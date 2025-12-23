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

- **Ligeros**: Consumen muy poca memoria (pocos KB vs ~1MB de platform threads).
- **Baratos de crear**: Se pueden crear millones sin degradar el rendimiento.
- **Transparentes**: Usan la misma API de Thread que conoces.
- **Optimizados para I/O**: Perfectos para operaciones de entrada/salida bloqueantes.

> 💡 En esencia, permiten escribir código bloqueante tradicional, pero con escalabilidad masiva.

### 💡 Concepto Clave

- `Platform Thread`: Wrapper de un OS Thread (pesado, limitado)
  ```
  Java Thread → OS Thread → CPU Core
  (1:1 mapping, limitado por OS)
  ```

- `Virtual Thread`: Hilo gestionado por la JVM (ligero, escalable)
  ```
  Java Virtual Thread → Carrier Thread (Platform) → CPU Core
  (M:N mapping, muchos virtual threads sobre pocos carrier threads)
  ```

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

## 📌 Cuándo usar Virtual Threads (Limitado por la I/O)

Ideal para tareas donde el hilo pasa la mayor parte del tiempo `esperando`.

- ✅ `Consultas a Bases de Datos`: Esperar la respuesta de un SELECT o UPDATE.
- ✅ `Llamadas HTTP/APIs`: Consultar servicios externos (REST, SOAP).
- ✅ `Sistemas de Archivos`: Leer o escribir logs y documentos pesados.
- ✅ `Microservicios`: Manejar miles de usuarios simultáneos sin agotar la RAM.

`Ejemplo real`: Un servidor Tomcat que antes colapsaba con 500 usuarios, con Virtual Threads puede soportar 50,000 con
el mismo hardware.

## ❌ Cuándo NO usarlos (Limitado por la CPU)

No sirven si la tarea requiere procesamiento puro y constante del procesador (ahí conviene usar platform threads o
ForkJoinPool).

- ❌ `Criptografía y Hash`: Minería de datos o cifrado de archivos grandes.
- ❌ `Compresión`: Comprimir carpetas a `.zip` o convertir formatos de video.
- ❌ `Cálculos Matemáticos`: Procesamiento de matrices o simulaciones complejas.
- ❌ `Uso de synchronized largo`: Si el código usa bloqueos antiguos (`synchronized`), el hilo virtual se `ataca` al
  real (Pinning) y pierde su ventaja.

`Ejemplo real`: Si tienes 8 núcleos de CPU, no importa si usas hilos virtuales; no podrás procesar más de 8 cálculos
matemáticos pesados exactamente al mismo tiempo.

> ⚠️ `Virtual Threads` no hacen más rápido el CPU, solo escalan mejor el I/O.

## ⚙️ Platform Threads vs Virtual Threads

### 🧱 Platform Threads (hilos tradicionales)

- Mapeados 1:1 con hilos del sistema operativo
- Costosos en memoria (~1MB por hilo)
- Bloquean recursos del SO cuando esperan I/O
- Limitados en cantidad (miles como máximo)

````java

@Slf4j
public class Platform {
    public static void main(String[] args) {
        demo1();
        demo2();
        demo3();
        demo4();
    }

    private static void demo1() {
        Thread thread = new Thread(() -> {
            log.info("demo1(): Platform thread");
        });
        thread.start();
    }

    private static void demo2() {
        Thread.ofPlatform().start(() -> {
            log.info("demo2(): Platform thread");
        });
    }

    private static void demo3() {
        // Estilo moderno con Builder
        Thread t = Thread.ofPlatform()
                .name("mi-hilo-proceso")
                .daemon(true)
                .priority(Thread.MAX_PRIORITY)
                .unstarted(() -> log.info("demo3(): Platform thread"));

        t.start();
    }

    // Usando un ExecutorService:
    private static void demo4() {
        try (ExecutorService executorService = Executors.newFixedThreadPool(5)) {
            executorService.submit(() -> {
                log.info("demo4(): Platform thread");
            });
        }
    }
}
````

| Método    | Tipo                | ¿Cuándo usarlo?                                                                                                                                                                                              |
|-----------|---------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `demo1()` | Constructor clásico | Tareas rápidas y simples. Es el estándar antiguo.                                                                                                                                                            |
| `demo2()` | Builder moderno     | Cuando quieres crear un hilo y lanzarlo de inmediato con la nueva API.                                                                                                                                       |
| `demo3()` | Builder configurado | El mejor para control total. Permite configurar nombre, prioridad y si es de tipo Daemon (hilo de segundo plano que no impide que el programa se cierre automáticamente al terminar las tareas principales). |
| `demo4()` | ExecutorService     | El mejor para aplicaciones reales. No creas hilos manualmente, sino que los "alquilas" de un pool para reutilizarlos.                                                                                        |

````bash
16:36:45.504 [mi-hilo-proceso] INFO dev.magadiflo.app.Platform -- demo3(): Platform thread
16:36:45.504 [Thread-1] INFO dev.magadiflo.app.Platform -- demo2(): Platform thread
16:36:45.504 [Thread-0] INFO dev.magadiflo.app.Platform -- demo1(): Platform thread
16:36:45.511 [pool-1-thread-1] INFO dev.magadiflo.app.Platform -- demo4(): Platform thread
````

| Método                | Estilo               | Notas técnicas                                                                                                              |
|-----------------------|----------------------|-----------------------------------------------------------------------------------------------------------------------------|
| `new Thread()`        | Tradicional (Legacy) | Es la forma clásica desde Java 1.0. Útil para instanciación rápida, pero menos flexible.                                    |
| `Thread.ofPlatform()` | Fluido (Moderno)     | Introducido en `Java 19/21`. Utiliza el patrón Builder, permitiendo configurar nombre, prioridad y daemon antes de iniciar. |

- La ventaja de `ofPlatform()` es que permite encadenar configuraciones de forma mucho más legible que el constructor
  tradicional.
- Ambos métodos consumen aproximadamente 1MB de memoria RAM por hilo (Stack Memory).
- Ambos son gestionados directamente por el Planificador del Sistema Operativo (OS Scheduler).
- Se recomienda usar la API moderna (ofPlatform()) por consistencia si también estás usando Virtual Threads
  (ofVirtual()) en tu proyecto.

### 🪶 Virtual Threads

- Administrados por la JVM.
- Consumen muy poca memoria.
- Se pueden crear por millones.
- Al bloquearse, liberan el hilo del SO.

En la siguiente clase, los tres métodos son equivalentes en el sentido de que todos lanzan la tarea dentro de un
`Virtual Thread`. Sin embargo, al igual que con los hilos de plataforma, existen matices importantes en la sintaxis y
el uso recomendado.

````java

@Slf4j
public class Virtual {
    public static void main(String[] args) throws InterruptedException {
        demo1();
        demo2();
        demo3();

        Thread.sleep(Duration.ofSeconds(1));
    }

    private static void demo1() {
        Thread.ofVirtual().start(() -> {
            log.info("demo1(): Virtual Thread");
        });
    }

    private static void demo2() {
        Thread.startVirtualThread(() -> {
            log.info("demo2(): Virtual Thread");
        });
    }

    // Usando un ExecutorService:
    private static void demo3() {
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            executorService.submit(() -> {
                log.info("demo3(): Virtual Thread");
            });
        }
    }
}
````

| Método    | Tipo                    | ¿Cuándo usarlo?                                                                                                                                                |
|-----------|-------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `demo1()` | Builder (`ofVirtual`)   | El más flexible. Permite configurar el nombre del hilo o características adicionales antes de iniciarlo. Es consistente con la API de hilos de plataforma.     |
| `demo2()` | Método estático directo | El más simple. Es un "shortcut" (acceso rápido) diseñado para lanzar una tarea rápido sin configuraciones extra. No permite poner nombres personalizados.      |
| `demo3()` | ExecutorService         | El estándar para aplicaciones. Ideal para manejar flujos de trabajo masivos. El `try-with-resources` asegura que el programa espere a que las tareas terminen. |

````bash
16:38:15.058 [virtual-27] INFO dev.magadiflo.app.Virtual -- demo2(): Virtual Thread
16:38:15.058 [virtual-25] INFO dev.magadiflo.app.Virtual -- demo1(): Virtual Thread
16:38:15.059 [virtual-32] INFO dev.magadiflo.app.Virtual -- demo3(): Virtual Thread
````

## 📝 Ejemplo Comparativo

### 🧱 1. Implementación con Platform Threads (Hilos de Plataforma)

En este enfoque, estamos utilizando un modelo de concurrencia basado en el `Sistema Operativo (SO)`.

#### Funcionamiento:

- `Pool Limitado`: Al definir `newFixedThreadPool(100)`, creamos un equipo de exactamente `100 hilos`. Cada uno de
  estos hilos es un `Platform Thread`, lo que significa que tiene una correspondencia `1:1` con un hilo real del
  kernel del sistema operativo.
- `Gestión por Lotes`: El ejecutor toma las primeras 100 tareas y las asigna a los 100 hilos. Mientras estas tareas
  ejecutan `simulateIO()` (espera de `1 segundo`), los hilos están `bloqueados`; no pueden hacer nada más
  porque el hilo del SO está atado a esa espera.
- `La Cola de Espera`: Las 9,900 tareas restantes se quedan en una cola de memoria (`LinkedBlockingQueue`). Solo cuando
  un hilo termina su tarea, regresa al pool para recoger la siguiente de la cola.
- `Resultado`: Para procesar `10,000` tareas de `1 segundo` cada una con `100 hilos`, el tiempo total será de
  `~100 segundos (1 minuto 40 segundos)`.

> Este modelo es costoso en memoria, ya que cada hilo de plataforma suele reservar aproximadamente `1MB de stack`
> fuera del heap de Java.

````java
// ❌ Platform Thread - Limitado y costoso
@Slf4j
public class PlatformThreadExample {

    public static void main(String[] args) {
        try (ExecutorService executor = Executors.newFixedThreadPool(100)) {
            for (int i = 0; i < 10_000; i++) {
                int taskId = i;
                executor.submit(() -> {
                    log.info("Task {} en {}", taskId, Thread.currentThread().getName());
                    simulateIO();
                });
            }
        }
        // ⚠️ Solo 100 tareas concurrentes, las demás esperan en cola
    }

    private static void simulateIO() {
        try {
            Thread.sleep(Duration.ofSeconds(1)); // Simula I/O bloqueante
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
````

````bash
12:16:51.139 [pool-1-thread-18] INFO dev.magadiflo.app.PlatformThreadExample -- Task 17 en pool-1-thread-18
12:16:51.146 [pool-1-thread-41] INFO dev.magadiflo.app.PlatformThreadExample -- Task 40 en pool-1-thread-41
12:16:51.139 [pool-1-thread-26] INFO dev.magadiflo.app.PlatformThreadExample -- Task 25 en pool-1-thread-26
12:16:51.145 [pool-1-thread-34] INFO dev.magadiflo.app.PlatformThreadExample -- Task 33 en pool-1-thread-34
12:16:51.139 [pool-1-thread-1] INFO dev.magadiflo.app.PlatformThreadExample -- Task 0 en pool-1-thread-1
12:16:51.145 [pool-1-thread-53] INFO dev.magadiflo.app.PlatformThreadExample -- Task 52 en pool-1-thread-53
...
12:16:51.160 [pool-1-thread-99] INFO dev.magadiflo.app.PlatformThreadExample -- Task 98 en pool-1-thread-99
12:16:51.161 [pool-1-thread-100] INFO dev.magadiflo.app.PlatformThreadExample -- Task 99 en pool-1-thread-100
12:16:51.161 [pool-1-thread-75] INFO dev.magadiflo.app.PlatformThreadExample -- Task 74 en pool-1-thread-75
...
12:18:31.266 [pool-1-thread-71] INFO dev.magadiflo.app.PlatformThreadExample -- Task 9997 en pool-1-thread-71
12:18:31.266 [pool-1-thread-26] INFO dev.magadiflo.app.PlatformThreadExample -- Task 9996 en pool-1-thread-26
12:18:31.266 [pool-1-thread-42] INFO dev.magadiflo.app.PlatformThreadExample -- Task 9995 en pool-1-thread-42
12:18:31.266 [pool-1-thread-2] INFO dev.magadiflo.app.PlatformThreadExample -- Task 9998 en pool-1-thread-2
12:18:31.266 [pool-1-thread-80] INFO dev.magadiflo.app.PlatformThreadExample -- Task 9999 en pool-1-thread-80
````

Le tomó aproximadamente `1 minuto 40 segundos` en finalizar la ejecución.

### 🪶 2. Implementación con Virtual Threads (Hilos Virtuales)

Este enfoque, introducido en `Java 21 (Project Loom)`, cambia las reglas del juego al desacoplar los hilos de Java
de los hilos del SO.

#### Funcionamiento:

- `Un hilo por tarea`: `newVirtualThreadPerTaskExecutor()` no usa un pool de tamaño fijo. Crea un hilo virtual nuevo
  para cada una de las `10,000 tareas` de forma casi instantánea.
- `Ejecución Masiva`: Las `10,000 tareas` intentan ejecutarse al mismo tiempo. A diferencia de los hilos de plataforma,
  los hilos virtuales son "baratos" (pesan apenas unos pocos KB).
- `Desbloqueo Inteligente`: Cuando la tarea llega a `simulateIO()` y ejecuta `Thread.sleep()`, el hilo virtual se
  "suspende" y se desprende del hilo real del sistema operativo (llamado `Carrier Thread`). Esto permite que el hilo
  real del sistema procese otros miles de hilos virtuales mientras el primero espera su segundo de pausa.
- `Resultado`: El tiempo total de ejecución será de aproximadamente `~1 segundo` (más una mínima latencia de gestión),
  ya que todas las esperas ocurren de forma paralela y no secuencial.

````java
// ✅ Virtual Thread - Escalable y eficiente
@Slf4j
public class VirtualThreadExample {
    public static void main(String[] args) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 10_000; i++) {
                int taskId = i;
                executor.submit(() -> {
                    log.info("Task {} en {}", taskId, Thread.currentThread().getName());
                    simulateIO();
                });
            }
        }
        // ✨ Las 10,000 tareas se ejecutan concurrentemente
    }

    private static void simulateIO() {
        try {
            Thread.sleep(Duration.ofSeconds(1)); // Simula I/O bloqueante
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
````

````bash
12:56:02.940 [virtual-28] INFO dev.magadiflo.app.VirtualThreadExample -- Task 2 en 
12:56:02.940 [virtual-30] INFO dev.magadiflo.app.VirtualThreadExample -- Task 4 en 
12:56:02.951 [virtual-37] INFO dev.magadiflo.app.VirtualThreadExample -- Task 11 en 
12:56:02.940 [virtual-32] INFO dev.magadiflo.app.VirtualThreadExample -- Task 6 en 
12:56:02.951 [virtual-41] INFO dev.magadiflo.app.VirtualThreadExample -- Task 14 en 
12:56:02.951 [virtual-43] INFO dev.magadiflo.app.VirtualThreadExample -- Task 16 en 
12:56:02.951 [virtual-45] INFO dev.magadiflo.app.VirtualThreadExample -- Task 18 en
...
12:56:03.772 [virtual-9980] INFO dev.magadiflo.app.VirtualThreadExample -- Task 9939 en 
12:56:03.772 [virtual-9988] INFO dev.magadiflo.app.VirtualThreadExample -- Task 9947 en 
12:56:03.773 [virtual-9993] INFO dev.magadiflo.app.VirtualThreadExample -- Task 9952 en 
12:56:03.774 [virtual-10013] INFO dev.magadiflo.app.VirtualThreadExample -- Task 9972 en 
12:56:03.775 [virtual-10027] INFO dev.magadiflo.app.VirtualThreadExample -- Task 9986 en 
12:56:03.775 [virtual-10035] INFO dev.magadiflo.app.VirtualThreadExample -- Task 9994 en 
````

El programa debería tardar `~1 segundo` en completar las 10000 tareas, aunque en nuestra ejecución real podríamos
ver algo como `2–3 segundos` si el `logging` ralentiza el proceso.

#### ¿Por qué el nombre del hilo virtual no se ve en el log?

La línea `log.info("Task {} en {}", taskId, Thread.currentThread().getName());` muestra algo como `Task 9915 en`
(vacío al final).

`La razón técnica`: Los hilos virtuales, por diseño, no tienen nombre por defecto.

- En un `FixedThreadPool`, Java les asigna nombres automáticamente como `pool-1-thread-1`.
- En `newVirtualThreadPerTaskExecutor()`, los hilos se crean de forma masiva y, para ahorrar memoria y tiempo, Java los
  deja con el nombre en blanco o null.

### 📊 Comparación clara

| Implementación                | Concurrencia           | Tiempo total esperado | Tiempo real observado |
|-------------------------------|------------------------|-----------------------|-----------------------|
| `Platform Threads` (pool 100) | Máx. 100 hilos nativos | ~100 segundos         | ~100 segundos         |
| `Virtual Threads` (Loom)      | 10000 hilos virtuales  | ~1 segundo            | ~0.8–1 segundo        |

### 🔑 Conclusión pedagógica:

- Con `platform threads`, el tiempo escala linealmente con el número de lotes (limitado por el tamaño del pool).
- Con `virtual threads`, el tiempo depende solo de la duración de la tarea, no del número de tareas, porque todas
  pueden ejecutarse concurrentemente.
