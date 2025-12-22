package dev.magadiflo.app.composition;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ThenCompose {
    public static void main(String[] args) throws InterruptedException {
        // pipeline asíncrono: Usuario -> Pedidos -> Detalles

        CompletableFuture<List<String>> orderDetailsFuture = CompletableFuture
                .supplyAsync(() -> getUser(1))
                // 'thenCompose' se usa cuando la siguiente función devuelve otro CompletableFuture.
                // Aplica un "aplanamiento" (flattening), evitando tener un CompletableFuture<CompletableFuture<String>>.
                .thenCompose(user -> CompletableFuture.supplyAsync(() -> getOrdersByUser(user)))
                // Encadenamos otra operación asíncrona dependiente de la anterior.
                .thenCompose(orders -> CompletableFuture.supplyAsync(() -> getOrderDetails(orders)));

        // Consumo del resultado final (la lista de detalles)
        orderDetailsFuture.thenAccept(details -> log.info("Proceso completado: {}", details));

        // Mantenemos el hilo main vivo para que los hilos del pool terminen su tarea.
        Thread.sleep(Duration.ofSeconds(1));
    }

    private static String getUser(int userId) {
        return "usuario-" + userId;
    }

    private static String getOrdersByUser(String user) {
        return "orders-" + user;
    }

    private static List<String> getOrderDetails(String orders) {
        return List.of("orders-details-" + orders);
    }
}
