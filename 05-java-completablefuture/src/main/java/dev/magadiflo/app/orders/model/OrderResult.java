package dev.magadiflo.app.orders.model;

public record OrderResult(String orderId,
                          boolean success,
                          String message,
                          String transactionId) {
    public static OrderResult success(String orderId, String message, String transactionId) {
        return new OrderResult(orderId, true, message, transactionId);
    }

    public static OrderResult failure(String orderId, String message, String transactionId) {
        return new OrderResult(orderId, false, message, transactionId);
    }
}
