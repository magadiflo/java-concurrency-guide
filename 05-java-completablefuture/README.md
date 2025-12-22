# ☕ Java - CompletableFuture

---

## 🎯 Introducción

`CompletableFuture` es una clase introducida en `Java 8` que representa una tarea asíncrona que puede completarse en el
futuro. Es una evolución del `Future` tradicional, proporcionando una API mucho más poderosa y flexible para
programación asíncrona y reactiva.

### ✨ Ventajas sobre Future tradicional:

- ✅ **Composición de operaciones:** Encadenar múltiples tareas asíncronas.
- ✅ **Manejo de errores robusto:** Recuperación y transformación de excepciones.
- ✅ **Callbacks:** Ejecutar código cuando la tarea se completa.
- ✅ **Combinación de resultados:** Unir múltiples operaciones asíncronas.
- ✅ **No bloqueante:** Evita el uso de `get()` bloqueante.

Se utiliza mucho en `backend` para llamadas a servicios externos, consultas a base de datos, o procesos que pueden
ejecutarse en paralelo sin bloquear el hilo principal.

💡 Idea clave:
> `CompletableFuture` representa el `resultado futuro` de una computación que puede completarse `más adelante`,
> de forma `no bloqueante`.

### 🧠 ¿Por qué no usar solo Thread o ExecutorService?

| Enfoque             | Limitaciones                                         |
|---------------------|------------------------------------------------------|
| `Thread`            | Bajo nivel, difícil de manejar errores y composición |
| `ExecutorService`   | Manejo manual de resultados (`Future.get()` bloquea) |
| `CompletableFuture` | ✔ Asíncrono ✔ No bloqueante ✔ Composición fluida     |

🚀 `CompletableFuture` **es ideal para flujos asíncronos complejos**, como llamadas a APIs, pipelines de datos o
procesos en background.

## 📚 API de CompletableFuture

### 📌 Creación de CompletableFuture

#### 1️⃣ `supplyAsync` - Ejecuta una tarea que devuelve un resultado.

````java

@Slf4j
public class SupplyAsync {
    public static void main(String[] args) throws InterruptedException {
        log.info("Inicia método main");

        // 1. Iniciamos la tarea asíncrona.
        // supplyAsync usa por defecto el ForkJoinPool.commonPool.
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            // Simula latencia de un servicio externo (E/O)
            try {
                Thread.sleep(Duration.ofSeconds(3));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Resultado de operación asíncrona";
        });

        // 2. Callback no bloqueante:
        // 'thenAccept' registra un consumidor que se ejecutará automáticamente
        // en cuanto el resultado esté disponible, sin detener el hilo principal.
        completableFuture.thenAccept(resultado -> log.info("Recibido: {}", resultado));

        // NOTA PARA DOCUMENTACIÓN: En un método main, el programa podría terminar
        // antes de recibir el resultado. En entornos reales (Servidores, APIs),
        // el flujo sigue vivo y el callback se dispara correctamente.

        log.info("Finaliza método main");
        Thread.sleep(Duration.ofSeconds(4));
    }
}
````

````bash
12:42:38.419 [main] INFO dev.magadiflo.app.creations.SupplyAsync -- Inicia método main
12:42:38.435 [main] INFO dev.magadiflo.app.creations.SupplyAsync -- Finaliza método main
12:42:41.436 [ForkJoinPool.commonPool-worker-1] INFO dev.magadiflo.app.creations.SupplyAsync -- Recibido: Resultado de operación asíncrona
````

Definiciones Clave

- `CompletableFuture.supplyAsync`: Inicia una tarea que devuelve un valor de forma asíncrona. Al no especificar un
  `Executor`, la tarea se delega automáticamente al `ForkJoinPool.commonPool`.


- `ForkJoinPool.commonPool`: Es el pool de hilos compartido por defecto en la JVM. Se encarga de ejecutar tareas
  asíncronas de forma eficiente, ajustando automáticamente el número de hilos según los núcleos disponibles en tu
  procesador para maximizar el rendimiento.


- `thenAccept(Consumer<? super T> action)`: Es un método de la etapa de finalización. Permite procesar el resultado del
  `Future` tan pronto como se completa, operando de manera no bloqueante.

#### 1️⃣ `runAsync` - Ejecuta una tarea sin devolver resultado.

````java

@Slf4j
public class RunAsync {
    public static void main(String[] args) throws InterruptedException {
        log.info("Inicia método main");

        // 1. Uso de runAsync para tareas que NO devuelven un valor (Runnable).
        // Al igual que supplyAsync, se ejecuta en el ForkJoinPool.commonPool por defecto.
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
            try {
                // Simulación de una tarea pesada (ej. envío de un correo o generación de logs)
                Thread.sleep(Duration.ofSeconds(5));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            log.info("Finalizando ejecución de tarea asíncrona");
        });

        /*
         * NOTA TÉCNICA: En aplicaciones de consola, el hilo principal (main) no espera
         * a los hilos del pool asíncrono. Usamos Thread.sleep aquí para mantener la
         * JVM viva el tiempo suficiente para que la tarea asíncrona complete su ejecución.
         */
        log.info("Finaliza método main");
        Thread.sleep(Duration.ofSeconds(6));
    }
}
````

````bash
12:44:17.148 [main] INFO dev.magadiflo.app.creations.RunAsync -- Inicia método main
12:44:17.160 [main] INFO dev.magadiflo.app.creations.RunAsync -- Finaliza método main
12:44:22.171 [ForkJoinPool.commonPool-worker-1] INFO dev.magadiflo.app.creations.RunAsync -- Finalizando ejecución de tarea asíncrona
````

El `runAsync` se utiliza para ejecutar tareas que tienen efectos secundarios (side effects) pero no retornan un objeto.
Ejemplos comunes:

- Guardar un log en un archivo.
- Enviar una notificación push o un email.
- Actualizar una caché local.

#### 1️⃣ `completedFuture` - Crear un Future ya completado con un valor.

````java

@Slf4j
public class CompletedFuture {
    public static void main(String[] args) {
        log.info("Inicia método main");
        // 1. Crea un CompletableFuture que ya nace en estado "Completado".
        // No inicia ninguna tarea en hilos secundarios; el valor ya está disponible.
        CompletableFuture<String> completableFuture = CompletableFuture.completedFuture("Valor inmediato");

        // 2. Al estar ya completado, el callback 'thenAccept' se ejecuta
        // inmediatamente en el hilo que realiza la llamada (en este caso, el main).
        completableFuture.thenAccept(resultado -> log.info("Procesando: {}", resultado));

        log.info("Finaliza método main");
    }
}
````

````bash
12:45:40.984 [main] INFO dev.magadiflo.app.creations.CompletedFuture -- Inicia método main
12:45:40.995 [main] INFO dev.magadiflo.app.creations.CompletedFuture -- Procesando: Valor inmediato
12:45:40.997 [main] INFO dev.magadiflo.app.creations.CompletedFuture -- Finaliza método main
````

¿Qué es `CompletableFuture.completedFuture`?

Es un método de fábrica que devuelve una instancia de `CompletableFuture` que ya contiene un resultado. Es un estado
final alcanzado de forma inmediata.

Casos de Uso Principales

- `Pruebas Unitarias (Mocking)`: Cuando necesitas simular una respuesta asíncrona en un test pero ya conoces el valor
  de retorno.
- `Optimización/Caché`: Si el dato que buscas ya está en memoria (caché), puedes devolverlo con `completedFuture` en
  lugar de disparar un proceso asíncrono innecesario.
- `Compatibilidad de API`: Cuando una interfaz te obliga a devolver un `CompletableFuture<T>`, pero tu implementación
  ya tiene el resultado listo.

Diferencia de Ejecución
> A diferencia de `supplyAsync` o `runAsync`, `no se utiliza` el `ForkJoinPool` inicialmente. Todo ocurre de forma
> `síncrona` a menos que se utilicen variantes asíncronas en el encadenamiento (como `thenAcceptAsync`).

### 📌 Especificando un Executor personalizado

````java

@Slf4j
public class CustomExecutor {
    public static void main(String[] args) throws InterruptedException {
        log.info("Inicio de método main");

        // 1. Definimos un pool de hilos personalizado.
        // Esto evita el uso del ForkJoinPool.commonPool y nos da control total
        // sobre la cantidad de hilos y el ciclo de vida.
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // 2. Pasamos el 'executorService' como segundo argumento.
        // Ahora la tarea se ejecutará en uno de los 10 hilos de nuestro pool.
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Iniciando tarea en: {}", Thread.currentThread().getName());
                Thread.sleep(Duration.ofSeconds(2));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Ejecución finalizada en pool personalizado";
        }, executorService);

        // 3. Callback para procesar el resultado
        completableFuture.thenAccept(log::info);

        // NOTA: Es vital cerrar el executorService para liberar recursos
        // y permitir que la JVM finalice correctamente.

        log.info("Fin del método main");
        Thread.sleep(Duration.ofSeconds(3));
        executorService.shutdown();
    }
}
````

````bash
12:46:59.074 [main] INFO dev.magadiflo.app.creations.CustomExecutor -- Inicio de método main
12:46:59.089 [main] INFO dev.magadiflo.app.creations.CustomExecutor -- Fin del método main
12:46:59.087 [pool-1-thread-1] INFO dev.magadiflo.app.creations.CustomExecutor -- Iniciando tarea en: pool-1-thread-1
12:47:01.093 [pool-1-thread-1] INFO dev.magadiflo.app.creations.CustomExecutor -- Ejecución finalizada en pool personalizado
````

¿Por qué usar un Executor personalizado?

Por defecto, `CompletableFuture` usa el `ForkJoinPool.commonPool()`. Sin embargo, en producción se prefiere un
`Executor` propio por tres razones:

1. `Aislamiento (Bulkhead)`: Si una tarea pesada bloquea todos los hilos del pool, no afectará a otras tareas
   asíncronas del sistema que usen el pool común.
2. `Control de Recursos`: Puedes definir exactamente cuántos hilos quieres asignar a un proceso específico
   (ej. 50 hilos para envío de correos, 10 para reportes).
3. `Monitoreo`: Los pools personalizados permiten trackear métricas como hilos activos, tareas en cola y tiempos de
   ejecución de forma más sencilla.

