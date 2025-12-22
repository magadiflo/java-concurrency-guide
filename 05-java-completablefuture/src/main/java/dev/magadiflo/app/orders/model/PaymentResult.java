package dev.magadiflo.app.orders.model;

public record PaymentResult(boolean success,
                            String transactionId,
                            String message) {
    public static PaymentResult success(String transactionId, String message) {
        return new PaymentResult(true, transactionId, message);
    }

    public static PaymentResult failure(String transactionId, String message) {
        return new PaymentResult(false, transactionId, message);
    }
}
