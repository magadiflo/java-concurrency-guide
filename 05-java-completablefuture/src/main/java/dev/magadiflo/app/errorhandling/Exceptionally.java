package dev.magadiflo.app.errorhandling;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

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