package com.pszymczyk.telldontaskkata.entity;

import java.math.BigDecimal;
import java.util.List;

public class Order {
    private BigDecimal total;
    private String currency;
    private List<OrderItem> items;
    private BigDecimal tax;
    private OrderStatus status;
    private int id;

    Order(BigDecimal total, String currency, List<OrderItem> items, BigDecimal tax, OrderStatus status) {
        this.total = total;
        this.currency = currency;
        this.items = items;
        this.tax = tax;
        this.status = status;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getCurrency() {
        return currency;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    private OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addItem(OrderItem orderItem) {
        items.add(orderItem);
        setTotal(total.add(orderItem.getTaxedAmount()));
        setTax(tax.add(orderItem.getTax()));
    }

    public boolean isShipped() {
        return getStatus().equals(OrderStatus.SHIPPED);
    }

    public boolean isRejected() {
        return getStatus().equals(OrderStatus.REJECTED);
    }

    public boolean isApproved() {
        return getStatus().equals(OrderStatus.APPROVED);
    }

    public boolean isCreated() {
        return getStatus().equals(OrderStatus.CREATED);
    }
}
