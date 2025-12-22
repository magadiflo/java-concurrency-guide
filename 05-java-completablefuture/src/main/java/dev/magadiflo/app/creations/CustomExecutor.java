package dev.magadiflo.app.creations;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class CustomExecutor {
    public static void main(String[] args) throws InterruptedException {
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
        Thread.sleep(Duration.ofSeconds(3));
        executorService.shutdown();
        log.info("Fin del código");
    }
}
