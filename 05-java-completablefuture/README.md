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

## 🔗 Composición Asíncrona

### 🔸 thenApply - Transformar el resultado

Aplica una función al resultado cuando se completa. Es síncrono respecto al resultado anterior.

````java

@Slf4j
public class ThenApply {

    public static void main(String[] args) throws InterruptedException {
        // 1. Iniciamos el pipeline asíncrono obteniendo la entidad 'User'.
        CompletableFuture<UserDTO> completableFuture = CompletableFuture
                .supplyAsync(() -> findById(1))
                // 2. Transformación de datos (Mapeo):
                // 'thenApply' recibe el resultado de la etapa anterior y lo transforma.
                // Es funcionalmente equivalente al .map() de los Streams de Java.
                .thenApply(user -> {
                    log.info("Transformando entidad a DTO para: {}", user.getName());
                    return new UserDTO(user.getName(), user.getEmail());
                });

        // 3. Consumimos el resultado final transformado.
        completableFuture.thenAccept(userDTO -> log.info("DTO recibido con éxito: {}", userDTO));

        // Mantenemos el hilo main vivo para visualizar la ejecución de los hilos secundarios.
        Thread.sleep(Duration.ofSeconds(1));
    }

    private static User findById(int userId) {
        // Simulación de acceso a persistencia
        return new User(userId, "Sam", "sam@gmail.com");
    }

    // Clases de apoyo (Entidad y Record)
    @AllArgsConstructor
    @Data
    static class User {
        int id;
        String name;
        String email;
    }

    record UserDTO(String name, String email) {
    }
}
````

````bash
13:07:40.292 [main] INFO dev.magadiflo.app.composition.ThenApply -- Transformando entidad a DTO para: Sam
13:07:40.299 [main] INFO dev.magadiflo.app.composition.ThenApply -- DTO recibido con éxito: UserDTO[name=Sam, email=sam@gmail.com] 
````

#### Qué es `thenApply`?

Es un método de `transformación`. Se utiliza cuando quieres realizar una operación sobre el resultado de un
`CompletableFuture` y necesitas que esa operación devuelva un nuevo valor.

Características principales:

- `Encadenamiento`: Permite construir flujos de procesamiento paso a paso.
- `Sincronía del paso`: Por defecto, la función dentro de `thenApply` se ejecuta en el mismo hilo que completó la tarea
  anterior (a menos que uses thenApplyAsync).
- `Retorno`: Siempre devuelve un `CompletableFuture<U>`, donde U es el nuevo tipo de dato transformado.

### 🔸 thenCompose - Encadenar operaciones asíncronas dependientes

Usado cuando la siguiente operación también es asíncrona y depende del resultado anterior. Evita el anidamiento de
`CompletableFutures` (`CompletableFuture<CompletableFuture<T>>`).

````java

@Slf4j
public class ThenCompose {
    public static void main(String[] args) throws InterruptedException {
        // pipeline asíncrono: Usuario -> Pedidos -> Detalles

        CompletableFuture<List<String>> orderDetailsFuture = CompletableFuture
                .supplyAsync(() -> getUser(1))
                // 'thenCompose' se usa cuando la siguiente función devuelve otro CompletableFuture.
                // Aplica un "aplanamiento" (flattening), evitando tener un CompletableFuture<CompletableFuture<String>>.
                .thenCompose(user -> CompletableFuture.supplyAsync(() -> getOrdersByUser(user)))
                // Encadenamos otra operación asíncrona dependiente de la anterior.
                .thenCompose(orders -> CompletableFuture.supplyAsync(() -> getOrderDetails(orders)));

        // Consumo del resultado final (la lista de detalles)
        orderDetailsFuture.thenAccept(details -> log.info("Proceso completado: {}", details));

        // Mantenemos el hilo main vivo para que los hilos del pool terminen su tarea.
        Thread.sleep(Duration.ofSeconds(1));
    }

    private static String getUser(int userId) {
        return "usuario-" + userId;
    }

    private static String getOrdersByUser(String user) {
        return "orders-" + user;
    }

    private static List<String> getOrderDetails(String orders) {
        return List.of("orders-details-" + orders);
    }
}
````

````bash
13:20:48.580 [ForkJoinPool.commonPool-worker-1] INFO dev.magadiflo.app.composition.ThenCompose -- Proceso completado: [orders-details-orders-usuario-1] 
````

#### ¿Por qué usar thenCompose?

Se utiliza para orquestar servicios dependientes. Si el `Servicio A` devuelve un `Future` y el `Servicio B`
también devuelve un `Future` basado en el resultado de A, `thenCompose` une ambos de forma que el resultado final sea
un solo `Future` plano.

### 🔑 Diferencia clave:

- `thenApply`: Transforma el resultado de forma síncrona (`T -> U`)
- `thenCompose`: Encadena otra operación asíncrona (`T -> CompletableFuture<U>`)

## ⚠️ Manejo de Errores

### 🔸 exceptionally - Recuperarse de errores

Proporciona un valor alternativo cuando ocurre una excepción.

````java

@Slf4j
public class Exceptionally {
    public static void main(String[] args) throws InterruptedException {
        log.info("Inicio del proceso asíncrono");

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(Duration.ofSeconds(2));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    // Simulación de un error basado en probabilidad
                    double random = Math.random();
                    if (random > 0.5) {
                        throw new RuntimeException("Fallo en la comunicación con el servicio [" + random + "]");
                    }
                    return "Datos obtenidos con éxito [" + random + "]";
                })
                /*
                 * 'exceptionally' funciona como un salvavidas.
                 * Si alguna etapa anterior lanza una excepción, este bloque la captura.
                 * Permite retornar un "fallback" (valor de recuperación) para que el flujo continúe.
                 */
                .exceptionally(ex -> {
                    log.error("Se produjo una excepción: {}", ex.getMessage());
                    return "Respuesta de respaldo (Fallback)";
                });

        // Consumimos el resultado, que será el éxito o el valor por defecto del exceptionally
        completableFuture.thenAccept(res -> log.info("Resultado final: {}", res));

        log.info("Fin del método main");
        // Espera para visualizar el comportamiento en consola
        Thread.sleep(Duration.ofSeconds(3));
    }
}
````

````bash
16:11:33.944 [main] INFO dev.magadiflo.app.errorhandling.Exceptionally -- Inicio del proceso asíncrono
16:11:33.960 [main] INFO dev.magadiflo.app.errorhandling.Exceptionally -- Fin del método main
16:11:35.972 [ForkJoinPool.commonPool-worker-1] ERROR dev.magadiflo.app.errorhandling.Exceptionally -- Se produjo una excepción: java.lang.RuntimeException: Fallo en la comunicación con el servicio [0.5582597376801333]
16:11:35.974 [ForkJoinPool.commonPool-worker-1] INFO dev.magadiflo.app.errorhandling.Exceptionally -- Resultado final: Respuesta de respaldo (Fallback)
````

#### ¿Qué es exceptionally?

Es un método de gestión de errores que te permite interceptar una excepción y transformar el flujo de error de vuelta a
un flujo de datos normal mediante un valor por defecto.

#### Características principales:

- `Recuperación`: Evita que la excepción se propague y detenga todo el pipeline.
- `Transformación`: Convierte un objeto de tipo `Throwable` en un valor del mismo tipo que esperaba el Future original.
- `Ubicación`: Generalmente se coloca al final de la cadena de métodos para capturar errores de cualquier etapa previa.

### 🔸 handle - Manejar tanto éxito como error

Permite procesar tanto el resultado exitoso como la excepción en un solo lugar. El método handle es el "navaja suiza"
del manejo de errores en `CompletableFuture`. A diferencia de `exceptionally`, que solo se ejecuta cuando algo sale
mal, `handle` siempre se ejecuta, sin importar si la etapa anterior terminó con éxito o con una excepción.

````java

@Slf4j
public class Handle {
    public static void main(String[] args) throws InterruptedException {
        log.info("Inicio del proceso con handle");

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(Duration.ofSeconds(2));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    if (Math.random() > 0.5) {
                        throw new RuntimeException("Fallo crítico en el cálculo");
                    }
                    return "Datos procesados correctamente";
                })
                /*
                 * 'handle' recibe dos parámetros: (resultado, excepción).
                 * Es una etapa de transformación BiFunction que se ejecuta SIEMPRE.
                 * Permite centralizar la lógica de éxito y error en un solo lugar.
                 */
                .handle((result, throwable) -> {
                    if (Objects.nonNull(throwable)) {
                        log.warn("Lógica de recuperación: El sistema falló.");
                        return "Fallback: " + throwable.getMessage();
                    }
                    // Si no hay error, podemos transformar el resultado exitoso
                    return "Resultado final -> " + result.toUpperCase();
                });

        completableFuture.thenAccept(log::info);

        log.info("Hilo principal libre (no bloqueado)");
        Thread.sleep(Duration.ofSeconds(3));
    }
}
````

````bash
16:30:36.387 [main] INFO dev.magadiflo.app.errorhandling.Handle -- Inicio del proceso con handle
16:30:36.403 [main] INFO dev.magadiflo.app.errorhandling.Handle -- Hilo principal libre (no bloqueado)
16:30:38.414 [ForkJoinPool.commonPool-worker-1] WARN dev.magadiflo.app.errorhandling.Handle -- Lógica de recuperación: El sistema falló.
16:30:38.415 [ForkJoinPool.commonPool-worker-1] INFO dev.magadiflo.app.errorhandling.Handle -- Fallback: java.lang.RuntimeException: Fallo crítico en el cálculo
````

#### ¿Qué es handle?

Es un método de post-procesamiento total. Recibe el resultado de la etapa anterior y la excepción (si la hubo). Si la
etapa anterior tuvo éxito, la excepción será `null`; si falló, el resultado será `null`.
