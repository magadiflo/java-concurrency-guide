package dev.magadiflo.app.lifecycle;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WaitingState {

    private static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            synchronized (lock) {
                try {
                    lock.wait(); // Espera indefinida
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();
        Thread.sleep(100); // Dar tiempo para que entre en wait

        log.info("Estado: {}", thread.getState());

        // Despertar el hilo
        synchronized (lock) {
            lock.notify();
        }
    }
}
