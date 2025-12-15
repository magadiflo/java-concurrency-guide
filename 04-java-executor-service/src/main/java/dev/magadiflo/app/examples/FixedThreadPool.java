package dev.magadiflo.app.examples;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class FixedThreadPool {
    public static void main(String[] args) {
        // Crear un pool fijo de 5 hilos
        ExecutorService executor = Executors.newFixedThreadPool(5);

        // Enviar tareas Runnable
        for (int i = 1; i <= 100; i++) {
            final int facturaId = i;
            executor.submit(() -> log.info("Procesando factura #{} en hilo: {}", facturaId, Thread.currentThread().getName()));
        }

        // Cerrar el ExecutorService
        executor.shutdown();
    }
}
