package dev.magadiflo.app.lifecycle;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TerminatedState {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
           log.info("Hilo ejecutándose...");
        });
        thread.start();
        thread.join(); // Espera a que termine
        log.info("Estado: {}", thread.getState());
    }
}
