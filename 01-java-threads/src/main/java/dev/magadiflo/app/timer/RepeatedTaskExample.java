package dev.magadiflo.app.timer;

import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public class RepeatedTaskExample {
    public static void main(String[] args) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            int count = 0;

            @Override
            public void run() {
                count++;
                log.info("Ejecutando #{} (Hilo: {})", count, Thread.currentThread().getName());
                if (count == 5) {
                    log.info("Se ha alcanzado el límite. Cancelando el temporizador...");
                    timer.cancel(); // Detiene todas las tareas programadas
                }
            }
        };

        // Ejecuta después de 3 segundos, repite cada 1 segundo.
        timer.schedule(task, 3000, 1000);

        log.info("Tarea programada. Esperando...");
    }
}
