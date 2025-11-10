package dev.magadiflo.app.volatilekeyword;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class WithVolatileExample {

    private static volatile boolean running = true; // Visibilidad garantizada

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            log.info("Hilo de trabajo iniciado...");
            while (running) {
                // Bucle de espera activa
            }
            log.info("El hilo de trabajo se ha detenido");
        };
        Thread thread = new Thread(task);
        thread.start();

        Thread.sleep(Duration.ofSeconds(2));
        log.info("Cambiando el valor de la variable \"running\" a false...");
        running = false; // Se propaga a todos los hilos
    }
}
