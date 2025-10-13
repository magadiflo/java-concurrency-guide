package dev.magadiflo.app.lifecycle;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RunnableState {
    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                log.info("Simulando trabajo...");
            }
        };
        Thread thread = new Thread(task);
        thread.start();

        Thread.sleep(100); // Dar tiempo para que inicie
        log.info("Estado: {}", thread.getState());
        thread.interrupt();
    }
}
