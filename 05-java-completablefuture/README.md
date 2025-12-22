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
    public static void main(String[] args) {
        // 1. Iniciamos la tarea asíncrona.
        // supplyAsync usa por defecto el ForkJoinPool.commonPool.
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            // Simula latencia de un servicio externo (E/O)
            return "Resultado de operación asíncrona";
        });

        // 2. Callback no bloqueante:
        // 'thenAccept' registra un consumidor que se ejecutará automáticamente
        // en cuanto el resultado esté disponible, sin detener el hilo principal.
        completableFuture.thenAccept(resultado -> log.info("Recibido: {}", resultado));

        // NOTA: En un método main, el programa podría terminar
        // antes de recibir el resultado. En entornos reales (Servidores, APIs),
        // el flujo sigue vivo y el callback se dispara correctamente.
    }
}
````

````bash
11:44:16.098 [main] INFO dev.magadiflo.app.creations.SupplyAsync -- Recibido: Resultado de operación asíncrona
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

````

````bash
$ 
````

#### 1️⃣ `completedFuture` - Crear un Future ya completado con un valor.


