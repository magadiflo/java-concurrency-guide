package dev.magadiflo.app.orders.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Order {

    private String orderId;
    private String productId;
    private int quantity;
    private BigDecimal amount;
    private String customerEmail;

}
