package dev.magadiflo.app;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class ThreadLocalBasic {

    // Creando un ThreadLocal con valor inicial
    private static ThreadLocal<Integer> threadLocalValue = ThreadLocal.withInitial(() -> 0);

    public static void main(String[] args) {
        // Creando tres hilos diferentes
        Thread t1 = new Thread(task(100), "hilo-1");
        Thread t2 = new Thread(task(200), "hilo-2");
        Thread t3 = new Thread(task(null), "hilo-3"); // Este hilo no establece valor, usa el inicial

        t1.start();
        t2.start();
        t3.start();
    }

    private static Runnable task(Integer value) {
        return () -> {
            if (!Objects.isNull(value)) {
                threadLocalValue.set(value);
            }
            log.info("{}: {}", Thread.currentThread().getName(), threadLocalValue.get());
        };
    }
}
