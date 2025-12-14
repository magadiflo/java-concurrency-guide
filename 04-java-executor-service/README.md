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
