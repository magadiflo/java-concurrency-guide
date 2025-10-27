package dev.magadiflo.app.synchronizd;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CashBox {

    private final Object lock = new Object();
    private int money = 0;

    public void deposit(int amount) {
        log.info("Procesando depósito...");

        // Región crítica protegida
        synchronized (lock) {
            this.money += amount;
        }
    }

    public int getMoney() {
        return this.money;
    }

    public static void main(String[] args) throws InterruptedException {
        CashBox cashBox = new CashBox();

        Runnable task = () -> {
            for (int i = 0; i < 5000; i++) {
                cashBox.deposit(1);
            }
        };

        Thread thread1 = new Thread(task, "hilo-1");
        Thread thread2 = new Thread(task, "hilo-2");

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        log.info("Valor final: {}", cashBox.getMoney()); // Siempre 10000
    }
}
