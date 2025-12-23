package dev.magadiflo.app;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// ❌ Platform Thread - Limitado y costoso
@Slf4j
public class PlatformThreadExample {

    public static void main(String[] args) {
        try (ExecutorService executor = Executors.newFixedThreadPool(100)) {
            for (int i = 0; i < 10_000; i++) {
                int taskId = i;
                executor.submit(() -> {
                    log.info("Task {} en {}", taskId, Thread.currentThread().getName());
                    simulateIO();
                });
            }
        }
        // ⚠️ Solo 100 tareas concurrentes, las demás esperan en cola
    }

    private static void simulateIO() {
        try {
            Thread.sleep(Duration.ofSeconds(1)); // Simula I/O bloqueante
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
