package com.pszymczyk.telldontaskkata.entity;

public class OrderItemFactory {

    public static OrderItem create(Product product, int quantity) {
        return new OrderItem(
                product,
                quantity,
                product.taxAmount(quantity),
                product.taxedAmount(quantity));
    }
}
