package dev.magadiflo.app.controller;

import dev.magadiflo.app.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/async")
    public CompletableFuture<String> generate() {
        // Devolvemos directamente el CompletableFuture
        return reportService.generateReport()
                .thenApply(result -> "Resultado: " + result);
    }

    @GetMapping("/async-fireforget")
    public String generateFireAndForget() {
        reportService.generateReport(); // 🚀 se lanza en otro hilo
        return "Reporte en proceso..."; // ✅ respuesta inmediata al cliente
    }
}
