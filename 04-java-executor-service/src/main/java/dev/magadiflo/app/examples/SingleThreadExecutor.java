package dev.magadiflo.app.examples;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class SingleThreadExecutor {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> log.info("Usuario login"));
        executor.submit(() -> log.info("Consulta BD"));
        executor.submit(() -> log.info("Usuario logout"));
    }
}
