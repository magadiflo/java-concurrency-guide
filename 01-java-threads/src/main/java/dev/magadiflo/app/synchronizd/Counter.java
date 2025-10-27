package dev.magadiflo.app.synchronizd;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Counter {

    private int value = 0;

    public synchronized void increment() {
        this.value++;
    }

    public int getValue() {
        return this.value;
    }

    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();

        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
            }
        };

        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        log.info("Valor final: {}", counter.getValue());
    }
}
