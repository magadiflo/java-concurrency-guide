package dev.magadiflo.app.threads;

import lombok.extern.slf4j.Slf4j;

import java.util.stream.IntStream;

@Slf4j
public class MyLambdaThread {
    public static void main(String[] args) {
        Thread thread1 = new Thread(task(), "hilo-1");
        Thread thread2 = new Thread(task(), "hilo-2");
        Thread thread3 = new Thread(task(), "hilo-3");

        thread1.start();
        thread2.start();
        thread3.start();
    }

    private static Runnable task() {
        return () -> {
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
        };
    }
}
