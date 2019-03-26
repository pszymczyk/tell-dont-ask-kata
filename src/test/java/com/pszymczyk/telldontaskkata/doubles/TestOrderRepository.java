package com.pszymczyk.telldontaskkata.doubles;

import com.pszymczyk.telldontaskkata.entity.Order;
import com.pszymczyk.telldontaskkata.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestOrderRepository implements OrderRepository {
    private Order insertedOrder;
    private List<Order> orders = new ArrayList<>();

    public Order getSavedOrder() {
        return insertedOrder;
    }

    public void save(Order order) {
        this.insertedOrder = order;
    }

    @Override
    public Order getById(UUID orderId) {
        return orders.stream().filter(o -> o.getId() == orderId).findFirst().get();
    }

    public void addOrder(Order order) {
        this.orders.add(order);
    }
}
