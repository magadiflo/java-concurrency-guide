package dev.magadiflo.app.orders.service;

import dev.magadiflo.app.orders.model.Order;
import dev.magadiflo.app.orders.model.OrderResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotificationService {

    public void sendNotification(Order order, OrderResult orderResult) {
        log.info("Enviando notificación a: {}", order.getCustomerEmail());
        simulateDelay(300);

        if (orderResult.success()) {
            log.info("Notificación enviada: Pedido confirmado: {}", orderResult.transactionId());
        } else {
            log.error("Notificación enviada: Pedido fallido: {}", orderResult.message());
        }
    }

    private void simulateDelay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
