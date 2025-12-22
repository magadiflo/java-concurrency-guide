package dev.magadiflo.app.creations;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class CompletedFuture {
    public static void main(String[] args) {
        // 1. Crea un CompletableFuture que ya nace en estado "Completado".
        // No inicia ninguna tarea en hilos secundarios; el valor ya está disponible.
        CompletableFuture<String> completableFuture = CompletableFuture.completedFuture("Valor inmediato");

        // 2. Al estar ya completado, el callback 'thenAccept' se ejecuta
        // inmediatamente en el hilo que realiza la llamada (en este caso, el main).
        completableFuture.thenAccept(resultado -> log.info("Procesando: {}", resultado));
    }
}
