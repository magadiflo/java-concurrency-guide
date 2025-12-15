package dev.magadiflo.app;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SistemaProcesoPedidos {
    public static void main(String[] args) {
        // Pool de 5 hilos para procesar pedidos
        ExecutorService executorOrders = Executors.newFixedThreadPool(5);

        // Pool separado para notificaciones
        ExecutorService executorNotifications = Executors.newCachedThreadPool();

        List<Integer> orders = Arrays.asList(101, 102, 103, 104, 105, 106);

        orders.forEach(orderId -> {
            executorOrders.submit(() -> {
                try {
                    // 1. Validar inventario
                    log.info("Validando inventario para pedido #{}", orderId);
                    Thread.sleep(1000);

                    // 2. Procesar pago
                    log.info("Procesando pago para pedido #{}", orderId);
                    Thread.sleep(1500);

                    // 3. Generar factura
                    log.info("Generando factura para pedido #{}", orderId);
                    Thread.sleep(800);

                    // 4. Enviar notificación (async)
                    executorNotifications.submit(() -> sendNotificationToClient(orderId));

                    log.info("Pedido #{} completado", orderId);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Error procesando pedido #{}", orderId);
                }
            });
        });

        // Cerrar ejecutores correctamente
        shutdownExecutor(executorOrders, "Orders");
        shutdownExecutor(executorNotifications, "Notifications");
    }

    private static void shutdownExecutor(ExecutorService executor, String name) {
        executor.shutdown();
        try {
            if (executor.awaitTermination(60, TimeUnit.SECONDS)) {
                log.info("Timeout en executor {}", name);
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static void sendNotificationToClient(Integer orderId) {
        log.info("Notificación enviada al cliente - Pedido #{}", orderId);
    }
}
