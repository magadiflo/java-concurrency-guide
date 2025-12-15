package dev.magadiflo.app.examples;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ScheduledThreadPool {
    public static void main(String[] args) {
        // Ejemplo: Generación de reportes diarios
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        // Ejecutar cada 24 horas
        scheduler.scheduleAtFixedRate(
                () -> log.info("Generando reporte diario..."),
                0,
                24,
                TimeUnit.HOURS
        );

        // Ejecutar con retraso inicial de 5 segundos
        scheduler.schedule(
                () -> log.info("Enviando notificación..."),
                5,
                TimeUnit.SECONDS
        );

        scheduler.shutdown();
    }
}
