package com.pszymczyk.telldontaskkata.entity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import com.pszymczyk.telldontaskkata.service.OrderApprovalRequest;

public class Order {
    private final OrderStatus status;

    private BigDecimal total;
    private String currency;
    private List<OrderItem> items;
    private BigDecimal tax;
    private int id;

    Order(BigDecimal total, String currency, List<OrderItem> items, BigDecimal tax, OrderStatus status) {
        this.total = total;
        this.currency = currency;
        this.items = items;
        this.tax = tax;
        this.status = status;
    }

    Order(int id, BigDecimal total, String currency, List<OrderItem> items, BigDecimal tax, OrderStatus status) {
        this.id = id;
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
        return status.equals(OrderStatus.SHIPPED);
    }

    public boolean isRejected() {
        return status.equals(OrderStatus.REJECTED);
    }

    public boolean isApproved() {
        return status.equals(OrderStatus.APPROVED);
    }

    public boolean isCreated() {
        return status.equals(OrderStatus.CREATED);
    }

    public Order apply(OrderApprovalRequest request) {
        return request.isApproved() ?
                new Order(this.id, this.total, this.currency, this.items, this.tax, OrderStatus.APPROVED) :
                new Order(this.id, this.total, this.currency, this.items, this.tax, OrderStatus.REJECTED);
    }

    public Order ship() {
        return new Order(this.id, this.total, this.currency, this.items, this.tax, OrderStatus.SHIPPED);
    }

    public Order reject() {
        return new Order(this.id, this.total, this.currency, this.items, this.tax, OrderStatus.REJECTED);
    }

    public Order approve() {
        return new Order(this.id, this.total, this.currency, this.items, this.tax, OrderStatus.APPROVED);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Order order = (Order) o;
        return id == order.id &&
                status == order.status &&
                Objects.equals(total, order.total) &&
                Objects.equals(currency, order.currency) &&
                Objects.equals(items, order.items) &&
                Objects.equals(tax, order.tax);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, total, currency, items, tax, id);
    }
}
