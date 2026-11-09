package dev.magadiflo.app.composition;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ThenCompose {

    record User(Integer id, String name, String email) {
    }

    record Order(Integer id, Integer userId, Double total) {
    }

    record OrderDetails(Integer id, Integer orderId, String productName) {
    }

    public static void main(String[] args) throws InterruptedException {
        // pipeline asíncrono: Usuario -> Pedidos -> Detalles

        CompletableFuture<List<OrderDetails>> orderDetailsFuture = CompletableFuture
                .supplyAsync(() -> getUser(1))
                // 'thenCompose' se usa cuando la siguiente función devuelve otro CompletableFuture.
                // Aplica un "aplanamiento" (flattening), evitando tener un CompletableFuture<CompletableFuture<String>>.
                .thenCompose(user -> getOrdersByUserAsync(user))
                // Encadenamos otra operación asíncrona dependiente de la anterior.
                .thenCompose(orders -> getOrderDetailsAsync(orders));

        // Consumo del resultado final
        orderDetailsFuture.thenAccept(details -> log.info("Proceso completado: {}", details));
        Thread.sleep(Duration.ofSeconds(1));
    }

    private static User getUser(Integer userId) {
        log.info("Consultando usuario...");
        return new User(userId, "Sam", "sam@gmail.com");
    }

    private static CompletableFuture<List<Order>> getOrdersByUserAsync(User user) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Consultando pedidos del usuario: {}", user.name());

            return List.of(
                    new Order(101, user.id(), 150.0),
                    new Order(102, user.id(), 250.0)
            );
        });
    }

    private static CompletableFuture<List<OrderDetails>> getOrderDetailsAsync(List<Order> orders) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Consultando detalles de los pedidos...");

            return orders.stream()
                    .map(order -> new OrderDetails(
                            order.id() * 10,
                            order.id(),
                            "Producto-" + order.id()
                    ))
                    .toList();
        });
    }
}
