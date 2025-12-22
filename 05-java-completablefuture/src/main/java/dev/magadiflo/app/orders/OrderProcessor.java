package dev.magadiflo.app.orders;

import dev.magadiflo.app.orders.model.Order;
import dev.magadiflo.app.orders.model.OrderResult;
import dev.magadiflo.app.orders.model.PaymentResult;
import dev.magadiflo.app.orders.service.InventoryService;
import dev.magadiflo.app.orders.service.NotificationService;
import dev.magadiflo.app.orders.service.PaymentService;
import dev.magadiflo.app.orders.service.ValidationService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class OrderProcessor {

    private final ValidationService validationService = new ValidationService();
    private final InventoryService inventoryService = new InventoryService();
    private final PaymentService paymentService = new PaymentService();
    private final NotificationService notificationService = new NotificationService();
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);


    /**
     * Procesa un pedido de manera asíncrona
     * 1. Valida el pedido
     * 2. Verifica stock
     * 3. Procesa pago
     * 4. Envía notificación
     */
    public CompletableFuture<OrderResult> processOrder(Order order) {
        log.info("Iniciando procesamiento de pedido: {}", order.getOrderId());

        return CompletableFuture
                // Paso 1: Validar pedido
                .supplyAsync(() -> {
                    this.validationService.validateOrder(order);
                    return order;
                }, this.executorService)

                // Paso 2: Verificar stock
                .thenCompose(validatedOrder -> CompletableFuture.supplyAsync(() -> {
                    this.inventoryService.checkStock(validatedOrder);
                    return validatedOrder;
                }, this.executorService))

                // Paso 3: Procesar pago
                .thenCompose(validatedOrder -> CompletableFuture.supplyAsync(() -> {
                    PaymentResult paymentResult = this.paymentService.processPayment(validatedOrder);
                    return OrderResult.success(validatedOrder.getOrderId(), "Pedido procesado exitosamente", paymentResult.transactionId());
                }, this.executorService))

                // Manejo de errores
                .handle((orderResult, throwable) -> {
                    if (throwable != null) {
                        log.error("Error procesando pedido: {}", throwable.getMessage());
                        return OrderResult.failure(order.getOrderId(), "Error: " + throwable.getCause().getMessage(), null);
                    }
                    return orderResult;
                })

                // Paso 4: Enviar notificación (siempre se ejecuta)
                .whenComplete((orderResult, throwable) -> {
                    this.notificationService.sendNotification(order, orderResult);
                });

    }

    public void shutdown() {
        this.executorService.shutdown();
    }

}
