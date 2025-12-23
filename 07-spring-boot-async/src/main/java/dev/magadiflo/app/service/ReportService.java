package dev.magadiflo.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class ReportService {

    @Async
    public CompletableFuture<String> generateReport() {
        try {
            log.info("Ejecutando generar report");
            Thread.sleep(5000); // ⏳ simulando tarea lenta (ej: consulta a BD o API externa)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(e); // ⚠️ buena práctica: propagar error
        }
        return CompletableFuture.completedFuture("Reporte listo");
    }
}
