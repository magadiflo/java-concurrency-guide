package dev.magadiflo.app.lifecycle;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlockedState {

    private static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Runnable task1 = () -> {
            synchronized (lock) {
                try {
                    Thread.sleep(5000); // Mantener el lock
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        Runnable task2 = () -> {
            synchronized (lock) { // Intentará adquirir el lock
                log.info("Thread 2 obtuvo el lock");
            }
        };

        Thread thread1 = new Thread(task1);
        Thread thread2 = new Thread(task2);

        thread1.start();
        Thread.sleep(100); // Asegurar que el thread1 tome el lock

        thread2.start();
        Thread.sleep(100); // Dar tiempo para que thread2 intente el lock

        log.info("Estado thread2: {}", thread2.getState());
        thread1.interrupt();
    }
}
