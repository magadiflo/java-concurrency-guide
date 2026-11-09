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
| `CompletableFuture` | ✔ Asíncrono ✔ No bloqueante ✔ Composición fluida  |

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
                log.info("Ejecutando operación asíncrona...");
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
17:29:48.021 [main] INFO dev.magadiflo.app.creations.SupplyAsync -- Inicia método main
17:29:48.035 [ForkJoinPool.commonPool-worker-1] INFO dev.magadiflo.app.creations.SupplyAsync -- Ejecutando operación asíncrona...
17:29:48.037 [main] INFO dev.magadiflo.app.creations.SupplyAsync -- Finaliza método main
17:29:51.038 [ForkJoinPool.commonPool-worker-1] INFO dev.magadiflo.app.creations.SupplyAsync -- Recibido: Resultado de operación asíncrona
````

Definiciones Clave

- `CompletableFuture.supplyAsync`: Programa la ejecución asíncrona de una tarea que devuelve un valor. **Cuando se
  invoca este método, el `Supplier` se envía al `Executor` para su ejecución.** Si no se especifica uno, se utiliza por
  defecto el `ForkJoinPool.commonPool`.
    - Una vez programada la tarea, `supplyAsync()` devuelve inmediatamente un `CompletableFuture` que representa el
      resultado futuro de la operación. Esto permite que el hilo que realizó la invocación continúe ejecutando las
      siguientes instrucciones sin esperar a que termine el `Supplier`.
    - `Importante`: La ejecución efectiva del `Supplier` no necesariamente comienza en el mismo instante de la
      invocación, ya que depende de la disponibilidad del hilo encargado de ejecutar la tarea.
- `ForkJoinPool.commonPool`: Es el pool de hilos compartido por defecto en la JVM. Se encarga de ejecutar tareas
  asíncronas de forma eficiente, utilizando un conjunto de hilos de trabajo.
- `thenAccept(Consumer<? super T> action)`: Es un método de la etapa de finalización. Permite procesar el resultado del
  `CompletableFuture` tan pronto como se completa correctamente, sin bloquear al hilo que registra el callback.
    - **Ejecución del callback:** En el ejemplo, podemos observar que tanto el mensaje
      `Ejecutando operación asíncrona...`
      como el mensaje `Recibido: Resultado de operación asíncrona` se imprimen desde el hilo
      `ForkJoinPool.commonPool-worker-1`. Esto sucede porque el callback se registró mientras el `Supplier` todavía
      estaba ejecutándose. Cuando este termina y completa el `CompletableFuture`, el callback puede ejecutarse
      directamente en ese mismo hilo.
        - Si el `CompletableFuture` ya estuviera completado cuando se invoca `thenAccept()`, el callback podría
          ejecutarse directamente en el hilo que registra dicha acción (por ejemplo, `main`).
        - **Importante:** `thenAccept()` no garantiza un hilo específico para ejecutar el callback. Su ejecución depende
          de si la operación ya se completó al registrar la acción.

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
2. `Control de Recursos`: Puedes definir exactamente cuántos hilos quieres asignar a un proceso específico (ej. 50 hilos
   para envío de correos, 10 para reportes).
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

Se utiliza cuando necesitamos encadenar operaciones asíncronas en las que cada operación depende del resultado de la
anterior y la función ejecutada devuelve otro `CompletableFuture`.

Su principal característica es que combina el futuro devuelto por la siguiente operación con la cadena existente,
evitando el anidamiento de futuros (`CompletableFuture<CompletableFuture<T>>`).

#### 🧪 Ejemplo práctico

Supongamos que necesitamos obtener los detalles de los pedidos de un usuario. Para ello, debemos realizar tres
consultas:

1. Obtener el usuario.
2. Obtener los pedidos asociados a ese usuario.
3. Obtener los detalles de dichos pedidos.

En este escenario, la primera operación devuelve directamente un objeto `User`, mientras que las siguientes devuelven
un `CompletableFuture`, simulando consultas asíncronas a servicios externos.

````java
@Slf4j
public class ThenCompose {

    record User(Integer id, String name, String email) {
    }

    record Order(Integer id, Integer userId, Double total) {
    }

    record OrderDetails(Integer id, Integer orderId, String productName) {
    }

    public static void main(String[] args) throws InterruptedException {
        // pipeline asíncrono: Usuario -> Pedidos -> Detalles

        CompletableFuture<List<OrderDetails>> orderDetailsFuture = CompletableFuture
                .supplyAsync(() -> getUser(1))
                // 'thenCompose' se usa cuando la siguiente función devuelve otro CompletableFuture.
                // Aplica un "aplanamiento" (flattening), evitando tener un CompletableFuture<CompletableFuture<String>>.
                .thenCompose(user -> getOrdersByUserAsync(user))
                // Encadenamos otra operación asíncrona dependiente de la anterior.
                .thenCompose(orders -> getOrderDetailsAsync(orders));

        // Consumo del resultado final
        orderDetailsFuture.thenAccept(details -> log.info("Proceso completado: {}", details));
        Thread.sleep(Duration.ofSeconds(1));
    }

    private static User getUser(Integer userId) {
        log.info("Consultando usuario...");
        return new User(userId, "Sam", "sam@gmail.com");
    }

    private static CompletableFuture<List<Order>> getOrdersByUserAsync(User user) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Consultando pedidos del usuario: {}", user.name());

            return List.of(
                    new Order(101, user.id(), 150.0),
                    new Order(102, user.id(), 250.0)
            );
        });
    }

    private static CompletableFuture<List<OrderDetails>> getOrderDetailsAsync(List<Order> orders) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Consultando detalles de los pedidos...");

            return orders.stream()
                    .map(order -> new OrderDetails(
                            order.id() * 10,
                            order.id(),
                            "Producto-" + order.id()
                    ))
                    .toList();
        });
    }
}
````

Salida obtenida:

````bash
18:10:59.604 [ForkJoinPool.commonPool-worker-1] INFO dev.magadiflo.app.composition.ThenCompose -- Consultando usuario...
18:10:59.613 [ForkJoinPool.commonPool-worker-1] INFO dev.magadiflo.app.composition.ThenCompose -- Consultando pedidos del usuario: Sam
18:10:59.618 [ForkJoinPool.commonPool-worker-1] INFO dev.magadiflo.app.composition.ThenCompose -- Consultando detalles de los pedidos...
18:10:59.624 [ForkJoinPool.commonPool-worker-1] INFO dev.magadiflo.app.composition.ThenCompose -- Proceso completado: [OrderDetails[id=1010, orderId=101, productName=Producto-101], OrderDetails[id=1020, orderId=102, productName=Producto-102]]
````

#### **🔍 ¿Qué sucede durante la ejecución?**

Analicemos el encadenamiento:

```java
CompletableFuture<List<OrderDetails>> orderDetailsFuture = CompletableFuture
        .supplyAsync(() -> getUser(1))
        .thenCompose(user -> getOrdersByUserAsync(user))
        .thenCompose(orders -> getOrderDetailsAsync(orders));
```

**1. Primera operación: Obtener el usuario**

```bash
CompletableFuture.supplyAsync(() -> getUser(1))
```

- `supplyAsync()` programa la ejecución de `getUser(1)` en el `ForkJoinPool.commonPool`, que es el executor utilizado
  por defecto.
- `getUser(1)` devuelve directamente un objeto `User`.
- Por tanto, el futuro resultante es un `CompletableFuture<User>`.

**2. Segunda operación: Obtener los pedidos**

```bash
.thenCompose(user -> getOrdersByUserAsync(user))
```

- `thenCompose()` espera a que se complete correctamente la etapa anterior y recibe directamente el objeto `User`.
- Ejecuta la función proporcionada, que invoca `getOrdersByUserAsync(user)`.
- Este método devuelve un `CompletableFuture<List<Order>>`.
- `thenCompose()` integra ese futuro en la cadena, evitando generar un
  `CompletableFuture<CompletableFuture<List<Order>>>`.

**3. Tercera operación: Obtener los detalles**

```bash
.thenCompose(orders -> getOrderDetailsAsync(orders))
```

- Recibe la lista de pedidos cuando se completa la operación anterior.
- Ejecuta `getOrderDetailsAsync(orders)`, que devuelve un `CompletableFuture<List<OrderDetails>>`.
- Nuevamente, `thenCompose()` integra ese futuro en la cadena.

**4. Resultado final**

El resultado de todo el encadenamiento es un único:

```java
CompletableFuture<List<OrderDetails>>
```

Este futuro representa la finalización de todas las operaciones encadenadas y contiene la lista de detalles obtenida al
finalizar el proceso.

#### 🔑 ¿Por qué utilizar `thenCompose`?

La razón principal es evitar el anidamiento de `CompletableFuture` cuando una función encadenada devuelve otro futuro.
Para entenderlo mejor, imaginemos que reemplazamos el primer `thenCompose()` por `thenApply()`:

```java
CompletableFuture<CompletableFuture<List<Order>>> ordersFuture = CompletableFuture
        .supplyAsync(() -> getUser(1))
        .thenApply(user -> getOrdersByUserAsync(user));
```

¿Qué sucede?

- `supplyAsync()` devuelve un `CompletableFuture<User>`.
- `thenApply()` recibe el objeto `User` y ejecuta `getOrdersByUserAsync(user)`.
- Este método devuelve un `CompletableFuture<List<Order>>`.
- `thenApply()` toma ese futuro como el resultado de la transformación, generando un futuro anidado.

El resultado es:

```java
CompletableFuture<CompletableFuture<List<Order>>>
```

En cambio, utilizando `thenCompose()`:

```java
CompletableFuture<List<Order>> ordersFuture = CompletableFuture
        .supplyAsync(() -> getUser(1))
        .thenCompose(user -> getOrdersByUserAsync(user));
```

Obtenemos directamente un `CompletableFuture<List<Order>>`, porque `thenCompose()` integra el futuro devuelto por la
función en la cadena de procesamiento.

#### 📌 Diferencia clave entre `thenApply` y `thenCompose`

La diferencia no está simplemente en que una operación dependa de otra, sino en **el tipo de dato que devuelve la
función que pasamos como argumento**.

| Método          | Comportamiento                                                     | Transformación             |
|-----------------|--------------------------------------------------------------------|----------------------------|
| `thenApply()`   | Transforma el resultado anterior y devuelve un valor directamente. | `T → U`                    |
| `thenCompose()` | Encadena una operación que devuelve otro `CompletableFuture`.      | `T → CompletableFuture<U>` |

- **`thenApply(Function<T, U>)`**: Se utiliza cuando queremos transformar un resultado en otro, por ejemplo, convertir
  un `User` en un `UserDTO`.
- **`thenCompose(Function<T, CompletableFuture<U>>)`**: Se utiliza cuando necesitamos ejecutar una operación que
  devuelve otro `CompletableFuture`, integrándolo en la cadena existente.

> **💡 Regla práctica:** Si la función que pasamos a `thenApply()` devuelve un objeto directamente, utilizamos
> `thenApply()`. Si devuelve un `CompletableFuture` que queremos integrar en la cadena, utilizamos `thenCompose()`.

> #### **📝 NOTA: Cuando la primera operación también devuelve un `CompletableFuture`**
>
> En nuestro ejemplo, `getUser()` devuelve directamente un objeto `User`. Por eso iniciamos la cadena utilizando
> `CompletableFuture.supplyAsync()`.
>
> Sin embargo, en aplicaciones reales, es habitual que los clientes encargados de consultar servicios externos ya
> devuelvan un `CompletableFuture<T>`.
>
> En ese escenario, podemos utilizar directamente el futuro devuelto por el primer método como punto de partida, sin
> necesidad de envolverlo en otro `supplyAsync()`.
>
> **Ejemplo:**
>
> ```java
> CompletableFuture<List<OrderDetails>> orderDetailsFuture = getUserAsync(1)
>         .thenCompose(user -> getOrdersByUserAsync(user))
>         .thenCompose(orders -> getOrderDetailsAsync(orders));
> ```
>
> En este caso, `getUserAsync(1)` devuelve un `CompletableFuture<User>`, por lo que podemos comenzar directamente el
> encadenamiento con `thenCompose()`.
>
> De esta manera, los tres métodos pueden devolver sus respectivos `CompletableFuture`, manteniendo una única cadena de
> operaciones asíncronas.

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

### 🔸whenComplete - Ejecutar acción sin modificar el resultado

Similar a `handle`, pero no transforma el resultado. Útil para logging o limpieza.

````java

@Slf4j
public class WhenComplete {
    public static void main(String[] args) throws InterruptedException {
        log.info("Inicio del proceso asíncrono");

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(Duration.ofSeconds(2));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return "Datos procesados";
                })
                /*
                 * 'whenComplete' actúa como un callback de monitoreo.
                 * Se ejecuta al finalizar la etapa anterior, ya sea con éxito o error.
                 * A diferencia de 'handle', NO puede cambiar el resultado ni el tipo del Future.
                 */
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        log.error("Log de auditoría: Fallo detectado -> {}", throwable.getMessage());
                        // La excepción sigue su curso, no se "consume" aquí.
                    } else {
                        log.info("Log de auditoría: Éxito alcanzado con resultado: {}", result);
                    }
                });

        // El valor que llega aquí es el original del supplyAsync,
        // regardless de lo que haya pasado en whenComplete.
        completableFuture.thenAccept(res -> log.info("Consumiendo resultado final: {}", res));

        log.info("Hilo principal sigue su ejecución...");
        Thread.sleep(Duration.ofSeconds(3));
    }
}
````

````bash
17:01:01.308 [main] INFO dev.magadiflo.app.errorhandling.WhenComplete -- Inicio del proceso asíncrono
17:01:01.326 [main] INFO dev.magadiflo.app.errorhandling.WhenComplete -- Hilo principal sigue su ejecución...
17:01:03.337 [ForkJoinPool.commonPool-worker-1] INFO dev.magadiflo.app.errorhandling.WhenComplete -- Log de auditoría: Éxito alcanzado con resultado: Datos procesados
17:01:03.339 [ForkJoinPool.commonPool-worker-1] INFO dev.magadiflo.app.errorhandling.WhenComplete -- Consumiendo resultado final: Datos procesados
````

#### ¿Qué es whenComplete?

Es un método de consumo pasivo. Se utiliza para ejecutar efectos secundarios (logging, métricas, cerrar conexiones) sin
alterar el resultado del pipeline asíncrono.

## 💻 Proyecto Práctico: Sistema de Procesamiento de Pedidos

### 📦 Descripción del proyecto

Crearemos un sistema que procesa pedidos de manera asíncrona:

1. Validar el pedido.
2. Verificar stock en inventario.
3. Procesar el pago.
4. Enviar notificación al cliente.

### 🗂️ Estructura del proyecto

````scss
src/
├── model/
│   ├── Order.java
│   ├── PaymentResult.java
│   └── OrderResult.java
├── service/
│   ├── ValidationService.java
│   ├── InventoryService.java
│   ├── PaymentService.java
│   └── NotificationService.java
└── OrderProcessor.java 
````

#### Order

````java

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Order {

    private String orderId;
    private String productId;
    private int quantity;
    private BigDecimal amount;
    private String customerEmail;

}
````

#### PaymentResult

````java
public record PaymentResult(boolean success,
                            String transactionId,
                            String message) {
    public static PaymentResult success(String transactionId, String message) {
        return new PaymentResult(true, transactionId, message);
    }

    public static PaymentResult failure(String transactionId, String message) {
        return new PaymentResult(false, transactionId, message);
    }
}
````

#### OrderResult

````java
public record OrderResult(String orderId,
                          boolean success,
                          String message,
                          String transactionId) {
    public static OrderResult success(String orderId, String message, String transactionId) {
        return new OrderResult(orderId, true, message, transactionId);
    }

    public static OrderResult failure(String orderId, String message, String transactionId) {
        return new OrderResult(orderId, false, message, transactionId);
    }
}
````

#### ValidationService

````java

@Slf4j
public class ValidationService {

    public boolean validateOrder(Order order) {
        log.info("Validando pedido: {}", order.getOrderId());
        simulateDelay(500);

        // validaciones básicas
        if (order.getOrderId() == null || order.getOrderId().isBlank()) {
            throw new IllegalArgumentException("Order ID no puede estar vacío");
        }

        if (order.getQuantity() <= 0) {
            throw new IllegalArgumentException("Cantidad debe ser mayor a 0");
        }

        if (order.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Monto debe ser mayor a 0");
        }

        log.info("Pedido validado correctamente");
        return true;
    }

    private void simulateDelay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
````

#### InventoryService

````java

@Slf4j
public class InventoryService {

    private final Map<String, Integer> inventory = new HashMap<>();

    {
        this.inventory.put("PROD-001", 100);
        this.inventory.put("PROD-002", 50);
        this.inventory.put("PROD-003", 200);
    }

    public boolean checkStock(Order order) {
        log.info("Verificando stock para producto: {}", order.getProductId());
        simulateDelay(800);

        int available = this.inventory.getOrDefault(order.getProductId(), -1);
        if (available == -1) {
            throw new IllegalArgumentException("Producto no encontrado:  " + order.getProductId());
        }

        if (available < order.getQuantity()) {
            throw new IllegalStateException("Stock insuficiente. Disponible: " + available + ", Requerido: " + order.getQuantity());
        }

        //Reducir stock
        int newStock = available - order.getQuantity();
        inventory.put(order.getProductId(), newStock);

        log.info("Stock verificado. Disponible: {}", newStock);
        return true;
    }

    private void simulateDelay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
````

````java

@Slf4j
public class PaymentService {

    public PaymentResult processPayment(Order order) {
        log.info("Procesando pago para pedido: {}", order.getOrderId());
        simulateDelay(1000);

        // Simular fallo aleatorio (10% de probabilidad)
        if (Math.random() < 0.1) {
            throw new RuntimeException("Error en procesamiento de pago: Gateway timeout");
        }

        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8);
        log.info("Pago procesado. Transacción ID: {}", transactionId);
        return PaymentResult.success(transactionId, "Pago exitoso");
    }

    private void simulateDelay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
````

#### NotificationService

````java

@Slf4j
public class NotificationService {

    public void sendNotification(Order order, OrderResult orderResult) {
        log.info("Enviando notificación a: {}", order.getCustomerEmail());
        simulateDelay(300);

        if (orderResult.success()) {
            log.info("Notificación enviada: Pedido confirmado: {}", orderResult.transactionId());
        } else {
            log.error("Notificación enviada: Pedido fallido: {}", orderResult.message());
        }
    }

    private void simulateDelay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
````

#### OrderProcessor

````java

@Slf4j
public class OrderProcessor {

    private final ValidationService validationService = new ValidationService();
    private final InventoryService inventoryService = new InventoryService();
    private final PaymentService paymentService = new PaymentService();
    private final NotificationService notificationService = new NotificationService();
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);


    /**
     * Procesa un pedido de manera asíncrona
     * 1. Valida el pedido
     * 2. Verifica stock
     * 3. Procesa pago
     * 4. Envía notificación
     */
    public CompletableFuture<OrderResult> processOrder(Order order) {
        log.info("Iniciando procesamiento de pedido: {}", order.getOrderId());

        return CompletableFuture
                // Paso 1: Validar pedido
                .supplyAsync(() -> {
                    this.validationService.validateOrder(order);
                    return order;
                }, this.executorService)

                // Paso 2: Verificar stock
                .thenCompose(validatedOrder -> CompletableFuture.supplyAsync(() -> {
                    this.inventoryService.checkStock(validatedOrder);
                    return validatedOrder;
                }, this.executorService))

                // Paso 3: Procesar pago
                .thenCompose(validatedOrder -> CompletableFuture.supplyAsync(() -> {
                    PaymentResult paymentResult = this.paymentService.processPayment(validatedOrder);
                    return OrderResult.success(validatedOrder.getOrderId(), "Pedido procesado exitosamente", paymentResult.transactionId());
                }, this.executorService))

                // Manejo de errores
                .handle((orderResult, throwable) -> {
                    if (throwable != null) {
                        log.error("Error procesando pedido: {}", throwable.getMessage());
                        return OrderResult.failure(order.getOrderId(), "Error: " + throwable.getCause().getMessage(), null);
                    }
                    return orderResult;
                })

                // Paso 4: Enviar notificación (siempre se ejecuta)
                .whenComplete((orderResult, throwable) -> {
                    this.notificationService.sendNotification(order, orderResult);
                });

    }

    public void shutdown() {
        this.executorService.shutdown();
    }

}
````

#### Main

````java

@Slf4j
public class Main {
    public static void main(String[] args) {
        OrderProcessor processor = new OrderProcessor();

        log.info("Ejemplo: procesamiento de un solo pedido");

        Order order1 = Order.builder()
                .orderId("ORD-001")
                .productId("PROD-001")
                .quantity(2)
                .amount(new BigDecimal("100.00"))
                .customerEmail("cliente@gmail.com")
                .build();

        CompletableFuture<OrderResult> feature1 = processor.processOrder(order1);

        // Obtener resultado (bloqueante, solo para demo)
        feature1
                .thenAccept(orderResult -> log.info("Resultado final: {}", orderResult))
                .join();

        processor.shutdown();
    }
}
````

````bash
18:09:31.786 [main] INFO dev.magadiflo.app.orders.Main -- Ejemplo: procesamiento de un solo pedido
18:09:31.793 [main] INFO dev.magadiflo.app.orders.OrderProcessor -- Iniciando procesamiento de pedido: ORD-001
18:09:31.803 [pool-1-thread-1] INFO dev.magadiflo.app.orders.service.ValidationService -- Validando pedido: ORD-001
18:09:32.309 [pool-1-thread-1] INFO dev.magadiflo.app.orders.service.ValidationService -- Pedido validado correctamente
18:09:32.310 [pool-1-thread-2] INFO dev.magadiflo.app.orders.service.InventoryService -- Verificando stock para producto: PROD-001
18:09:33.122 [pool-1-thread-2] INFO dev.magadiflo.app.orders.service.InventoryService -- Stock verificado. Disponible: 98
18:09:33.125 [pool-1-thread-3] INFO dev.magadiflo.app.orders.service.PaymentService -- Procesando pago para pedido: ORD-001
18:09:34.219 [pool-1-thread-3] INFO dev.magadiflo.app.orders.service.PaymentService -- Pago procesado. Transacción ID: TXN-fda2f3d2
18:09:34.220 [pool-1-thread-3] INFO dev.magadiflo.app.orders.service.NotificationService -- Enviando notificación a: cliente@gmail.com
18:09:34.530 [pool-1-thread-3] INFO dev.magadiflo.app.orders.service.NotificationService -- Notificación enviada: Pedido confirmado: TXN-fda2f3d2
18:09:34.531 [pool-1-thread-3] INFO dev.magadiflo.app.orders.Main -- Resultado final: OrderResult[orderId=ORD-001, success=true, message=Pedido procesado exitosamente, transactionId=TXN-fda2f3d2] 
````
