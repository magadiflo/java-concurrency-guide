package dev.magadiflo.app.timer;

import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public class SingleTaskExample {
    public static void main(String[] args) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                log.info("Ejecutando tarea... (hilo: {})", Thread.currentThread().getName());

                timer.cancel(); // Finaliza este timer, descartando cualquier tarea programada.
            }
        };

        // Ejecutar la tarea después de 5 segundos (5000 ms)
        timer.schedule(task, 5000);

        log.info("Tarea programada. Esperando...");
    }
}
