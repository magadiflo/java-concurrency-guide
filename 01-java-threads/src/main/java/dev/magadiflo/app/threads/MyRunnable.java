package dev.magadiflo.app.threads;

import lombok.extern.slf4j.Slf4j;

import java.util.stream.IntStream;

@Slf4j
public class MyRunnable implements Runnable {
    @Override
    public void run() {
        log.info("Inicia ejecución del hilo {}", Thread.currentThread().getName());

        IntStream.range(0, 3)
                .forEach(value -> {
                    try {
                        Thread.sleep((long) (Math.random() * 1000));
                        log.info("realizando tarea... índice: {}, hilo actual: {}", value, Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });

        log.info("Fin del hilo {}", Thread.currentThread().getName());
    }

    public static void main(String[] args) {
        Thread thread1 = new Thread(new MyRunnable(), "hilo-1");
        Thread thread2 = new Thread(new MyRunnable(), "hilo-2");
        Thread thread3 = new Thread(new MyRunnable(), "hilo-3");

        thread1.start();
        thread2.start();
        thread3.start();
    }
}
