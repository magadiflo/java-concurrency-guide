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

| Tipo                        | Descripción                    | Uso recomendado                                  |
|-----------------------------|--------------------------------|--------------------------------------------------|
| `newFixedThreadPool(n)`     | Pool de tamaño fijo            | Microservicios, aplicaciones con carga constante |
| `newCachedThreadPool()`     | Crea hilos según demanda       | Aplicaciones IO-bound, scripts de automatización |
| `newSingleThreadExecutor()` | Un solo hilo                   | Procesos secuenciales que deben mantener orden   |
| `newScheduledThreadPool(n)` | Ejecución periódica o diferida | Cron jobs, tareas programadas                    |

Ejemplo:

````java
ExecutorService executor = Executors.newFixedThreadPool(4);
````

## 🔎 Métodos principales de ExecutorService

Algunos métodos útiles que ofrece `ExecutorService`:

- `execute(Runnable task)`: Ejecuta una tarea en segundo plano sin esperar un resultado.
- `submit(Callable task)`: Igual que execute, pero devuelve un resultado con Future.
- `shutdown()`: Detiene el ExecutorService después de terminar las tareas en curso.
- `shutdownNow()`: Intenta detener todas las tareas de inmediato.
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

