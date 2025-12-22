package dev.magadiflo.app.orders.service;

import dev.magadiflo.app.orders.model.Order;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class ValidationService {

    public boolean validateOrder(Order order) {
        log.info("Validando pedido: {}", order.getOrderId());
        simulateDelay(500);

        // validaciones básicas
        if (order.getOrderId() == null || order.getOrderId().isBlank()) {
            throw new IllegalArgumentException("Order ID no puede estar vacío");
        }

        if (order.getQuantity() <= 0) {
            throw new IllegalArgumentException("Cantidad debe ser mayor a 0");
        }

        if (order.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Monto debe ser mayor a 0");
        }

        log.info("Pedido validado correctamente");
        return true;
    }

    private void simulateDelay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
