package com.pszymczyk.telldontaskkata.entity;

import java.math.BigDecimal;

public class OrderItem {

    private final Product product;
    private final int quantity;
    private final BigDecimal taxedAmount;
    private final BigDecimal tax;

    OrderItem(Product product, int quantity, BigDecimal tax, BigDecimal taxedAmount) {
        this.product = product;
        this.quantity = quantity;
        this.tax = tax;
        this.taxedAmount = taxedAmount;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getTaxedAmount() {
        return taxedAmount;
    }

    public BigDecimal getTax() {
        return tax;
    }
}
