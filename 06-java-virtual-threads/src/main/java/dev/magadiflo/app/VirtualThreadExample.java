package dev.magadiflo.app;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// ✅ Virtual Thread - Escalable y eficiente
@Slf4j
public class VirtualThreadExample {
    public static void main(String[] args) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 10_000; i++) {
                int taskId = i;
                executor.submit(() -> {
                    log.info("Task {} en {}", taskId, Thread.currentThread().getName());
                    simulateIO();
                });
            }
        }
        // ✨ Las 10,000 tareas se ejecutan concurrentemente
    }

    private static void simulateIO() {
        try {
            Thread.sleep(Duration.ofSeconds(1)); // Simula I/O bloqueante
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
