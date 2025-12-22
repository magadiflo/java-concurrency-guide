package dev.magadiflo.app.creations;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

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
