package dev.magadiflo.app.errorhandling;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

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
