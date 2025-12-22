package dev.magadiflo.app.orders;

import dev.magadiflo.app.orders.model.Order;
import dev.magadiflo.app.orders.model.OrderResult;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class Main {
    public static void main(String[] args) {
        OrderProcessor processor = new OrderProcessor();

        log.info("Ejemplo: procesamiento de un solo pedido");

        Order order1 = Order.builder()
                .orderId("ORD-001")
                .productId("PROD-001")
                .quantity(2)
                .amount(new BigDecimal("100.00"))
                .customerEmail("cliente@gmail.com")
                .build();

        CompletableFuture<OrderResult> feature1 = processor.processOrder(order1);

        // Obtener resultado (bloqueante, solo para demo)
        feature1
                .thenAccept(orderResult -> log.info("Resultado final: {}", orderResult))
                .join();

        processor.shutdown();
    }
}
