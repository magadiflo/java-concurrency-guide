package dev.magadiflo.app.errorhandling;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

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
