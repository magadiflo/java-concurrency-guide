package dev.magadiflo.app.orders.service;

import dev.magadiflo.app.orders.model.Order;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class InventoryService {

    private final Map<String, Integer> inventory = new HashMap<>();

    {
        this.inventory.put("PROD-001", 100);
        this.inventory.put("PROD-002", 50);
        this.inventory.put("PROD-003", 200);
    }

    public boolean checkStock(Order order) {
        log.info("Verificando stock para producto: {}", order.getProductId());
        simulateDelay(800);

        int available = this.inventory.getOrDefault(order.getProductId(), -1);
        if (available == -1) {
            throw new IllegalArgumentException("Producto no encontrado:  " + order.getProductId());
        }

        if (available < order.getQuantity()) {
            throw new IllegalStateException("Stock insuficiente. Disponible: " + available + ", Requerido: " + order.getQuantity());
        }

        //Reducir stock
        int newStock = available - order.getQuantity();
        inventory.put(order.getProductId(), newStock);

        log.info("Stock verificado. Disponible: {}", newStock);
        return true;
    }

    private void simulateDelay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
