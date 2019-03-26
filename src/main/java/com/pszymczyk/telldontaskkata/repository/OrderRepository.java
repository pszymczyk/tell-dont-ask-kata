package com.pszymczyk.telldontaskkata.repository;

import java.util.UUID;

import com.pszymczyk.telldontaskkata.entity.Order;

public interface OrderRepository {
    void save(Order order);

    Order getById(UUID orderId);
}
