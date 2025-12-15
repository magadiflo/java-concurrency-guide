# 🧵 Java ExecutorService

---

El `ExecutorService` es una interfaz de alto nivel en Java que facilita la gestión de hilos.  
En lugar de crear y manejar manualmente objetos `Thread`, `ExecutorService` provee un mecanismo más flexible
y escalable para ejecutar tareas concurrentes. Es decir, permite administrar hilos de forma eficiente, evitando
crearlos manualmente con `new Thread()`, lo cual es costoso, difícil de escalar y propenso a errores.

## 🧩 ¿Qué es ExecutorService?

`ExecutorService` es una `interfaz` que abstrae la creación, asignación y manejo de hilos. En lugar de crear hilos
individuales, delegamos las tareas a un `pool de threads`, lo que permite:

- 🚀 Mejor rendimiento
- 📦 Reutilización de hilos
- 🧘‍♂️ Evitar fugas de hilos
- 📊 Control sobre la ejecución (shutdown, cancelación, timeouts)
- 🛡️ Manejo más seguro de concurrencia

## 🏗️ ¿Por qué no usar directamente `new Thread()`?

Problemas del enfoque tradicional:

- ❌ Crear muchos hilos satura la CPU.
- ❌ No existe control del número de hilos.
- ❌ No hay forma sencilla de manejar errores.
- ❌ No hay forma sencilla de esperar resultados.
- ❌ Difícil escalabilidad.

Ventajas del `ExecutorService` frente a `Thread`:

- ✅ **Escalabilidad**: reutiliza hilos en lugar de crear nuevos.
- ✅ **Control**: permite cancelar tareas, esperar resultados y manejar excepciones.
- ✅ **Flexibilidad**: soporta diferentes estrategias de ejecución (single-thread, fixed pool, cached pool, scheduled).
- ✅ **Integración**: se usa ampliamente en aplicaciones empresariales, servidores web y sistemas distribuidos.

Por eso, en entornos reales (backend, microservicios, sistemas de trading, servicios web, etc.), siempre se usa
`ExecutorService`.

## 🛠️ ¿Cómo obtener un ExecutorService?

La forma más común es a través de `Executors`:

📌 Tipos de Pools

| Tipo                        | Descripción                                            | Uso recomendado                                  |
|-----------------------------|--------------------------------------------------------|--------------------------------------------------|
| `newFixedThreadPool(n)`     | Pool con un número fijo de hilos                       | Microservicios, aplicaciones con carga constante |
| `newCachedThreadPool()`     | Crea hilos bajo demanda y reutiliza los existentes     | Aplicaciones IO-bound, scripts de automatización |
| `newSingleThreadExecutor()` | Usa un único hilo para ejecutar tareas secuencialmente | Procesos secuenciales que deben mantener orden   |
| `newScheduledThreadPool(n)` | Permite programar tareas con retrasos o periodicidad   | Cron jobs, tareas programadas                    |

Ejemplo:

````java
ExecutorService executor = Executors.newFixedThreadPool(4);
````

## 🔎 Métodos principales de ExecutorService

Algunos métodos útiles que ofrece `ExecutorService`:

- `execute(Runnable task)`: Ejecuta una tarea en segundo plano sin esperar un resultado.
- `submit(Callable task)`: Igual que execute, pero devuelve un resultado con Future.
- `shutdown()`: Detiene el ExecutorService después de terminar las tareas en curso.
- `shutdownNow()`: Intenta detener todas las tareas activas inmediatamente.
- `invokeAll(Collection<Callable> tasks)`: Ejecuta varias tareas y devuelve una lista de objetos Future con los
  resultados.
- `invokeAny(Collection<Callable> tasks)`: Ejecuta varias tareas y devuelve el resultado de la primera que termine.

> 🧹 `Muy importante`: **cerrar el ExecutorService**  
> Si no se llama a `shutdown()`, la aplicación seguirá viva por los hilos del pool.

## 🏗️ Ejemplos sobre los tipos de ExecutorService

### 1️⃣ FixedThreadPool

En este ejemplo se utiliza un `FixedThreadPool` con un tamaño fijo de `5 hilos` para procesar un conjunto de
`100 tareas` que representan la atención de facturas.

Aunque se envían muchas tareas al `ExecutorService`, **solo 5 se ejecutan en paralelo al mismo tiempo**. Las demás
quedan encoladas y se van ejecutando conforme uno de los hilos queda libre. Esto permite **controlar el nivel de
concurrencia**, evitando la creación excesiva de hilos y protegiendo los recursos del sistema.

> 📌 En escenarios reales, este enfoque es común en procesos batch, microservicios o sistemas financieros donde se
> requiere **procesar grandes volúmenes de trabajo de forma controlada y predecible**.

````java

@Slf4j
public class FixedThreadPool {
    public static void main(String[] args) {
        // Crear un pool fijo de 5 hilos
        ExecutorService executor = Executors.newFixedThreadPool(5);

        // Enviar tareas Runnable
        for (int i = 1; i <= 100; i++) {
            final int facturaId = i;
            executor.submit(() -> log.info("Procesando factura #{} en hilo: {}", facturaId, Thread.currentThread().getName()));
        }

        // Cerrar el ExecutorService
        executor.shutdown();
    }
}
````

````bash
19:48:19.283 [pool-1-thread-3] INFO dev.magadiflo.app.examples.FixedThreadPool -- Procesando factura #3 en hilo: pool-1-thread-3
19:48:19.283 [pool-1-thread-4] INFO dev.magadiflo.app.examples.FixedThreadPool -- Procesando factura #4 en hilo: pool-1-thread-4
19:48:19.283 [pool-1-thread-2] INFO dev.magadiflo.app.examples.FixedThreadPool -- Procesando factura #2 en hilo: pool-1-thread-2
19:48:19.283 [pool-1-thread-1] INFO dev.magadiflo.app.examples.FixedThreadPool -- Procesando factura #1 en hilo: pool-1-thread-1
19:48:19.283 [pool-1-thread-5] INFO dev.magadiflo.app.examples.FixedThreadPool -- Procesando factura #5 en hilo: pool-1-thread-5
19:48:19.290 [pool-1-thread-4] INFO dev.magadiflo.app.examples.FixedThreadPool -- Procesando factura #8 en hilo: pool-1-thread-4
19:48:19.290 [pool-1-thread-1] INFO dev.magadiflo.app.examples.FixedThreadPool -- Procesando factura #9 en hilo: pool-1-thread-1
..
19:48:19.293 [pool-1-thread-3] INFO dev.magadiflo.app.examples.FixedThreadPool -- Procesando factura #99 en hilo: pool-1-thread-3
19:48:19.293 [pool-1-thread-2] INFO dev.magadiflo.app.examples.FixedThreadPool -- Procesando factura #100 en hilo: pool-1-thread-2 
````

### ⚖️ `execute()` vs `submit()` usando `Runnable`

Cuando se envía una tarea `Runnable` a un `ExecutorService`, **`execute()` y `submit()` ejecutan la tarea de forma muy
similar**, pero existe una diferencia clave:

- `execute(Runnable)`  
  🔹 Ejecuta la tarea y **no retorna ningún valor**.  
  🔹 Si ocurre una excepción, esta se propaga al hilo del executor.


- `submit(Runnable)`  
  🔹 Ejecuta la tarea y **retorna un `Future<?>`**, aunque el `Runnable` no produzca resultado.  
  🔹 Las excepciones quedan encapsuladas dentro del `Future`.

📌 Regla práctica (la que se usa en la vida real)

- Usa `execute()` cuando solo quieres “dispara y olvida”.
- Usa `submit()` cuando:
    - quieres capturar errores
    - podrías necesitar el Future
    - o sabes que ese código puede evolucionar

En muchos equipos backend se estandariza `submit()` por seguridad, incluso con `Runnable`, para no perder excepciones
silenciosamente.

### 2️⃣ CachedThreadPool

En este ejemplo se utiliza un `CachedThreadPool`, el cual **crea hilos dinámicamente según la demanda** y reutiliza
aquellos que quedan libres. A diferencia de un `FixedThreadPool`, **no existe un límite fijo de hilos**, por lo que
pueden ejecutarse muchas tareas en paralelo si el sistema lo permite.

El resultado muestra cómo se crean múltiples hilos (`pool-1-thread-*`) para atender rápidamente las solicitudes
simuladas de una API. Este tipo de pool es adecuado para **tareas cortas y altamente concurrentes**, especialmente en
escenarios *IO-bound* como llamadas a servicios externos o procesamiento de requests HTTP.

⚠️ Debe usarse con cuidado en producción, ya que un volumen elevado de tareas puede provocar la creación excesiva de
hilos y afectar el rendimiento del sistema.

````java

@Slf4j
public class CachedThreadPool {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        for (int i = 1; i <= 50; i++) {
            final int requestApi = i;
            executor.submit(() -> log.info("Procesando Request API #{} en hilo: {}", requestApi, Thread.currentThread().getName()));
        }

        executor.shutdown();
    }
}
````

````bash
20:24:02.019 [pool-1-thread-30] INFO dev.magadiflo.app.examples.CachedThreadPool -- Procesando Request API #30 en hilo: pool-1-thread-30
20:24:02.026 [pool-1-thread-36] INFO dev.magadiflo.app.examples.CachedThreadPool -- Procesando Request API #36 en hilo: pool-1-thread-36
20:24:02.026 [pool-1-thread-48] INFO dev.magadiflo.app.examples.CachedThreadPool -- Procesando Request API #48 en hilo: pool-1-thread-48
20:24:02.019 [pool-1-thread-27] INFO dev.magadiflo.app.examples.CachedThreadPool -- Procesando Request API #27 en hilo: pool-1-thread-27
20:24:02.026 [pool-1-thread-41] INFO dev.magadiflo.app.examples.CachedThreadPool -- Procesando Request API #41 en hilo: pool-1-thread-41
20:24:02.019 [pool-1-thread-19] INFO dev.magadiflo.app.examples.CachedThreadPool -- Procesando Request API #19 en hilo: pool-1-thread-19
20:24:02.019 [pool-1-thread-6] INFO dev.magadiflo.app.examples.CachedThreadPool -- Procesando Request API #6 en hilo: pool-1-thread-6
...
20:24:02.019 [pool-1-thread-4] INFO dev.magadiflo.app.examples.CachedThreadPool -- Procesando Request API #4 en hilo: pool-1-thread-4
20:24:02.020 [pool-1-thread-18] INFO dev.magadiflo.app.examples.CachedThreadPool -- Procesando Request API #18 en hilo: pool-1-thread-18
````

### 3️⃣ SingleThreadExecutor

En este ejemplo se emplea un `SingleThreadExecutor`, el cual garantiza que **todas las tareas se ejecuten de forma
secuencial en un único hilo**.

Aunque se envían varias tareas, estas se procesan **en el mismo orden en que fueron enviadas**, reutilizando siempre el
mismo hilo (`pool-1-thread-1`). Esto asegura consistencia y evita problemas de concurrencia.

📌 Es ideal para escenarios donde el **orden de ejecución es crítico**, como auditorías, logs, transacciones simples o
flujos que no deben ejecutarse en paralelo.

````java

@Slf4j
public class SingleThreadExecutor {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> log.info("Usuario login"));
        executor.submit(() -> log.info("Consulta BD"));
        executor.submit(() -> log.info("Usuario logout"));

        executor.shutdown();
    }
}
````

````bash
20:27:22.634 [pool-1-thread-1] INFO dev.magadiflo.app.examples.SingleThreadExecutor -- Usuario login
20:27:22.639 [pool-1-thread-1] INFO dev.magadiflo.app.examples.SingleThreadExecutor -- Consulta BD
20:27:22.639 [pool-1-thread-1] INFO dev.magadiflo.app.examples.SingleThreadExecutor -- Usuario logout
````

### 4️⃣ ScheduledThreadPool

Aquí se emplea un `ScheduledThreadPool` para ejecutar tareas **de forma programada o periódica**, similar a un cron.

Se observa un caso de ejecución repetitiva (`scheduleAtFixedRate`) para la generación de reportes y otro de ejecución
diferida (`schedule`) para enviar una notificación tras un retraso inicial.

Este tipo de executor es ideal para **tareas recurrentes**, mantenimientos automáticos o procesos programados dentro
de una aplicación.

🕒 Permite manejar tiempos sin bloquear el hilo principal.

- `scheduleAtFixedRate(...)`. Se ejecuta de forma periódica, en este caso cada 24 horas, después de la primera
  ejecución.
- `schedule(...)`. Se ejecuta una sola vez, luego del tiempo de retraso indicado (5 segundos en el ejemplo).

📌 En este código, la tarea de `“Enviando notificación…”` se ejecuta solo una vez y no vuelve a ejecutarse, ni después de
24 horas ni junto con la tarea periódica.

Si quisieras que esa notificación también se ejecute de forma periódica, tendrías que usar `scheduleAtFixedRate()` o
`scheduleWithFixedDelay()`.

````java

@Slf4j
public class ScheduledThreadPool {
    public static void main(String[] args) {
        // Ejemplo: Generación de reportes diarios
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        // Ejecutar cada 24 horas
        scheduler.scheduleAtFixedRate(
                () -> log.info("Generando reporte diario..."),
                0,
                24,
                TimeUnit.HOURS
        );

        // Ejecutar con retraso inicial de 5 segundos
        scheduler.schedule(
                () -> log.info("Enviando notificación..."),
                5,
                TimeUnit.SECONDS
        );

        scheduler.shutdown();
    }
}
````

````bash
20:38:28.834 [pool-1-thread-1] INFO dev.magadiflo.app.examples.ScheduledThreadPool -- Generando reporte diario...
20:38:33.845 [pool-1-thread-2] INFO dev.magadiflo.app.examples.ScheduledThreadPool -- Enviando notificación... 
````

## 🧾 Ejemplo: Procesamiento concurrente de pedidos con múltiples ExecutorService

En este ejemplo se simula un **sistema de procesamiento de pedidos** utilizando dos `ExecutorService` independientes,
cada uno con una responsabilidad clara.

El `FixedThreadPool` de 5 hilos se encarga del **flujo principal del pedido** (validación de inventario, pago y
facturación), lo que permite procesar varios pedidos en paralelo de forma controlada. Cuando un pedido termina su flujo
principal, se delega el envío de la notificación a un `CachedThreadPool`, evitando bloquear el procesamiento de otros
pedidos.

📌 Este patrón es común en sistemas reales de e-commerce y microservicios, donde las tareas críticas y las tareas
secundarias (como notificaciones) se separan para **mejorar el rendimiento, la escalabilidad y la experiencia del
usuario**.

````java

@Slf4j
public class SistemaProcesoPedidos {
    public static void main(String[] args) {
        // Pool de 5 hilos para procesar pedidos
        ExecutorService executorOrders = Executors.newFixedThreadPool(5);

        // Pool separado para notificaciones
        ExecutorService executorNotifications = Executors.newCachedThreadPool();

        List<Integer> orders = Arrays.asList(101, 102, 103, 104, 105, 106);

        orders.forEach(orderId -> {
            executorOrders.submit(() -> {
                try {
                    // 1. Validar inventario
                    log.info("Validando inventario para pedido #{}", orderId);
                    Thread.sleep(1000);

                    // 2. Procesar pago
                    log.info("Procesando pago para pedido #{}", orderId);
                    Thread.sleep(1500);

                    // 3. Generar factura
                    log.info("Generando factura para pedido #{}", orderId);
                    Thread.sleep(800);

                    // 4. Enviar notificación (async)
                    executorNotifications.submit(() -> sendNotificationToClient(orderId));

                    log.info("Pedido #{} completado", orderId);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Error procesando pedido #{}", orderId);
                }
            });
        });

        // Cerrar ejecutores correctamente
        shutdownExecutor(executorOrders, "Orders");
        shutdownExecutor(executorNotifications, "Notifications");
    }

    private static void shutdownExecutor(ExecutorService executor, String name) {
        executor.shutdown();
        try {
            if (executor.awaitTermination(60, TimeUnit.SECONDS)) {
                log.info("Timeout en executor {}", name);
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static void sendNotificationToClient(Integer orderId) {
        log.info("Notificación enviada al cliente - Pedido #{}", orderId);
    }
}
````

````bash
23:05:10.654 [pool-1-thread-4] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Validando inventario para pedido #104
23:05:10.654 [pool-1-thread-3] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Validando inventario para pedido #103
23:05:10.654 [pool-1-thread-5] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Validando inventario para pedido #105
23:05:10.654 [pool-1-thread-2] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Validando inventario para pedido #102
23:05:10.654 [pool-1-thread-1] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Validando inventario para pedido #101
23:05:11.673 [pool-1-thread-1] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Procesando pago para pedido #101
23:05:11.673 [pool-1-thread-5] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Procesando pago para pedido #105
23:05:11.673 [pool-1-thread-2] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Procesando pago para pedido #102
23:05:11.673 [pool-1-thread-4] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Procesando pago para pedido #104
23:05:11.673 [pool-1-thread-3] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Procesando pago para pedido #103
23:05:13.185 [pool-1-thread-4] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Generando factura para pedido #104
23:05:13.185 [pool-1-thread-5] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Generando factura para pedido #105
23:05:13.185 [pool-1-thread-3] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Generando factura para pedido #103
23:05:13.185 [pool-1-thread-2] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Generando factura para pedido #102
23:05:13.185 [pool-1-thread-1] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Generando factura para pedido #101
23:05:13.989 [pool-1-thread-2] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Pedido #102 completado
23:05:13.989 [pool-1-thread-4] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Pedido #104 completado
23:05:13.989 [pool-1-thread-1] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Pedido #101 completado
23:05:13.989 [pool-1-thread-3] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Pedido #103 completado
23:05:13.989 [pool-1-thread-5] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Pedido #105 completado
23:05:13.990 [pool-1-thread-2] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Validando inventario para pedido #106
23:05:13.990 [pool-2-thread-2] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Notificación enviada al cliente - Pedido #103
23:05:13.990 [pool-2-thread-1] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Notificación enviada al cliente - Pedido #102
23:05:13.990 [pool-2-thread-4] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Notificación enviada al cliente - Pedido #104
23:05:13.990 [pool-2-thread-5] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Notificación enviada al cliente - Pedido #101
23:05:13.990 [pool-2-thread-3] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Notificación enviada al cliente - Pedido #105
23:05:15.002 [pool-1-thread-2] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Procesando pago para pedido #106
23:05:16.515 [pool-1-thread-2] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Generando factura para pedido #106
23:05:17.322 [pool-1-thread-2] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Pedido #106 completado
23:05:17.323 [pool-2-thread-3] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Notificación enviada al cliente - Pedido #106
23:05:17.323 [main] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Timeout en executor Orders
23:05:17.325 [main] INFO dev.magadiflo.app.SistemaProcesoPedidos -- Timeout en executor Notifications 
````
