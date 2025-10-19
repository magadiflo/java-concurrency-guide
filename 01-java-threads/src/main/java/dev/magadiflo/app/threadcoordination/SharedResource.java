package dev.magadiflo.app.threadcoordination;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SharedResource {
    private boolean available = false;

    public synchronized void produce(int count) {
        while (this.available) {
            try {
                log.info("produce - wait #{}", count);
                wait(); // Espera hasta que el recurso sea consumido
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        log.info("Produciendo recurso #{}", count);
        this.available = true;
        notify(); // Despierta al consumidor
    }

    public synchronized void consume(int count) {
        while (!this.available) {
            try {
                log.info("consume - wait #{}", count);
                wait(); // Espera hasta que haya un recurso disponible
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        log.info("Consumiendo recurso #{}", count);
        this.available = false;
        notify(); // Despierta al productor
    }

    public static void main(String[] args) {
        SharedResource resource = new SharedResource();

        Thread producer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                resource.produce(i);
            }
        });

        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                resource.consume(i);
            }
        });

        producer.start();
        consumer.start();
    }
}
