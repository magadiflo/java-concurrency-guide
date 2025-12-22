package dev.magadiflo.app.creations;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

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

        // NOTA PARA DOCUMENTACIÓN: En un método main, el programa podría terminar
        // antes de recibir el resultado. En entornos reales (Servidores, APIs),
        // el flujo sigue vivo y el callback se dispara correctamente.
    }
}