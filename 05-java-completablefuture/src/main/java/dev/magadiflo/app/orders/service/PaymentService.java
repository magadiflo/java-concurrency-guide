package dev.magadiflo.app.orders.service;

import dev.magadiflo.app.orders.model.Order;
import dev.magadiflo.app.orders.model.PaymentResult;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class PaymentService {

    public PaymentResult processPayment(Order order) {
        log.info("Procesando pago para pedido: {}", order.getOrderId());
        simulateDelay(1000);

        // Simular fallo aleatorio (10% de probabilidad)
        if (Math.random() < 0.1) {
            throw new RuntimeException("Error en procesamiento de pago: Gateway timeout");
        }

        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8);
        log.info("Pago procesado. Transacción ID: {}", transactionId);
        return PaymentResult.success(transactionId, "Pago exitoso");
    }

    private void simulateDelay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
