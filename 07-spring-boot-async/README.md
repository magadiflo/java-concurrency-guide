# ⚡ Programación Asíncrona en Spring Boot con @Async y CompletableFuture

Para profundizar en el uso de programación asíncrona en Spring Boot con `@EnableAsync` y `@Async`, puedes consultar el
siguiente repositorio:

[🔗 Ejemplo 01: Sistema de Procesamiento de Pedidos E-commerce](https://github.com/magadiflo/design-patterns/blob/main/07.spring_boot_patrones_comportamiento.md#observer)

En ese repositorio se implementa el patrón Observer utilizando:

- Ejecución asíncrona con `@Async`.
- Configuración de un `Executor` personalizado.
- Publicación y escucha de eventos con `ApplicationEventPublisher` y `@EventListener`.

Ese repositorio sirve como material de apoyo para entender mejor cómo Spring maneja la asincronía y el procesamiento en
segundo plano.

---

## Introducción

La anotación `@Async` permite ejecutar métodos de forma asíncrona en `Spring Boot`, delegando su ejecución a un pool de
hilos. Combinada con `CompletableFuture`, ofrece una forma sencilla y poderosa de manejar tareas en segundo plano y
componer resultados sin bloquear el hilo principal.

## 🧠 ¿Qué es programación asíncrona?

La `programación asíncrona` consiste en `iniciar una tarea y continuar con la ejecución sin esperar a que termine`.
El resultado se obtiene más adelante, cuando la tarea finaliza.

> 💡 *Es ideal para operaciones lentas como llamadas HTTP, acceso a BD o envío de correos.*

## 🚀 ¿Qué es `@Async`?

`@Async` es una anotación de Spring que indica que un método:

- Se ejecutará en un `hilo separado`.
- No bloqueará el hilo que lo invoca.
- Usará un `Executor` configurado en el contexto de Spring.

````java

@Async
public void processTask() {
// lógica en segundo plano
}
````

⚠️ *Sin configuración adicional, Spring usa un `SimpleAsyncTaskExecutor` (no recomendado para producción).*

## 🔧 Habilitar `@Async`

Primero, se debe habilitar el soporte para asincronía:

````java

@EnableAsync
@Configuration
public class AsyncConfig {
}
````

## 🧵 Configurar un Executor personalizado (Recomendado)

````java

@EnableAsync
@Configuration
public class AsyncConfig {
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }
}
````

💡*Esto permite controlar el uso de recursos y evitar crear hilos sin límite.*

## 🔮 ¿Qué es `CompletableFuture`?

`CompletableFuture` es una clase de Java que representa:

- Un resultado futuro.
- Que se completará de forma asíncrona.
- Y que puede componerse con otras tareas.

````java
CompletableFuture<String> future;
````

## 🤝 @Async + CompletableFuture

Cuando un método anotado con `@Async` retorna un `CompletableFuture`, Spring:

- Ejecuta el método en otro hilo.
- Devuelve inmediatamente un `CompletableFuture`.
- Completa el resultado cuando termina la ejecución.

Ejemplo sencillo:

````java

@Slf4j
@Service
public class ReportService {

    @Async
    public CompletableFuture<String> generateReport() {
        try {
            log.info("Ejecutando generar report");
            Thread.sleep(5000); // ⏳ simulando tarea lenta (ej: consulta a BD o API externa)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(e); // ⚠️ buena práctica: propagar error
        }
        return CompletableFuture.completedFuture("Reporte listo");
    }
}
````

Uso desde un controlador:

````java

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/async")
    public CompletableFuture<String> generate() {
        // Devolvemos directamente el CompletableFuture
        return reportService.generateReport()
                .thenApply(result -> "Resultado: " + result);
    }
}
````

El controlador expone un endpoint REST que aprovecha la programación asíncrona de Spring Boot mediante `@Async` y
`CompletableFuture`.

- El método devuelve un `CompletableFuture`, lo que significa que la respuesta se enviará al cliente cuando la tarea
  asíncrona se complete.
- Uso de `thenApply`, permite transformar el resultado del servicio (Reporte listo) en un mensaje más completo
  (Resultado: Reporte listo).

````bash
$ curl -v http://localhost:8080/api/v1/reports/async
>
< HTTP/1.1 200
< Content-Type: text/plain;charset=UTF-8
< Content-Length: 24
< Date: Tue, 23 Dec 2025 23:36:17 GMT
<
Resultado: Reporte listo
````

Vemos que el método `generateReport()` se ejecuta en otro hilo del `taskExecutor` configurado `async-1`.

````bash
2025-12-23T18:36:12.258-05:00  INFO 18136 --- [07-spring-boot-async] [        async-1] d.magadiflo.app.service.ReportService    : Ejecutando generar report 
````

### 💡 Ejemplo laboral

Este patrón es útil cuando el cliente necesita el resultado final de una tarea asíncrona, como:

- Generación de reportes PDF.
- Consultas a APIs externas que deben combinarse.
- Procesamiento de datos que retorna un valor concreto.

### 🎨 Notas visuales

- 🧵 El servicio corre en un hilo separado (`async-1`).
- ⏳ El controlador espera el resultado antes de responder.
- ✅ Ideal para endpoints que deben devolver el resultado final de la operación.

## ⚡ Respuesta inmediata (fire‑and‑forget)

En algunos escenarios no necesitamos devolver el resultado final al cliente, sino solo confirmar que la tarea se inició.
En este caso, el método asíncrono seguirá ejecutándose en segundo plano, pero el cliente recibe la respuesta de
inmediato:

````java

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/async-fireforget")
    public String generateFireAndForget() {
        reportService.generateReport(); // 🚀 se lanza en otro hilo
        return "Reporte en proceso..."; // ✅ respuesta inmediata al cliente
    }
}
````

- 🧵 El servicio corre en un hilo separado gracias a `@Async`.
- ⏳ El controlador no espera el `CompletableFuture`.
- 🚀 Útil para tareas como envío de correos, logging, o procesos que no requieren respuesta inmediata al cliente.

````bash
$ curl -v http://localhost:8080/api/v1/reports/async-fireforget
>
* Request completely sent off
< HTTP/1.1 200
< Content-Type: text/plain;charset=UTF-8
< Content-Length: 21
< Date: Tue, 23 Dec 2025 23:44:54 GMT
<
Reporte en proceso... 
````

````bash
2025-12-23T18:44:54.280-05:00  INFO 2448 --- [07-spring-boot-async] [        async-1] d.magadiflo.app.service.ReportService    : Ejecutando generar report 
````
