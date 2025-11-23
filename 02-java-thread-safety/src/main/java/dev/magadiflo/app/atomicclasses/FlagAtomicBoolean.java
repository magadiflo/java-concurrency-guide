package dev.magadiflo.app.atomicclasses;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class FlagAtomicBoolean {

    private static final AtomicBoolean flag = new AtomicBoolean(false);

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            // Intentamos activar el flag solo si está en 'false'
            if (flag.compareAndSet(false, true)) {
                log.info("Hilo {} activó el flag", Thread.currentThread().getName());
            } else {
                log.info("Hilo {} no pudo activarlo", Thread.currentThread().getName());
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        log.info("Valor final del flag: {}", flag.get());
    }
}
