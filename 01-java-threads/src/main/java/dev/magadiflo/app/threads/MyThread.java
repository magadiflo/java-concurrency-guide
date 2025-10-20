package dev.magadiflo.app.threads;

import lombok.extern.slf4j.Slf4j;

import java.util.stream.IntStream;

@Slf4j
public class MyThread extends Thread {

    public MyThread(String name) {
        super(name);
    }

    @Override
    public void run() {
        log.info("Inicia ejecución del hilo {}", Thread.currentThread().getName());

        IntStream.range(0, 10)
                .forEach(value -> log.info("{}, {}", value, Thread.currentThread().getName()));

        log.info("Fin del hilo {}", Thread.currentThread().getName());
    }

    public static void main(String[] args) {
        Thread thread1 = new MyThread("hilo-1");
        thread1.start();

        Thread thread2 = new MyThread("hilo-2");
        thread2.start();

        log.info("{}", thread1.getState());
        log.info("{}", thread2.getState());
    }
}
