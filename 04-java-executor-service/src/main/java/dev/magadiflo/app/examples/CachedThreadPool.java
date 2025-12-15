package dev.magadiflo.app.examples;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class CachedThreadPool {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        for (int i = 1; i <= 50; i++) {
            final int requestApi = i;
            executor.submit(() -> log.info("Procesando Request API #{} en hilo: {}", requestApi, Thread.currentThread().getName()));
        }

        executor.shutdown();
    }
}
