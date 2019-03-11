package com.pszymczyk.telldontaskkata.repository;

import com.pszymczyk.telldontaskkata.entity.Order;

public interface OrderRepository {
    void save(Order order);

    Order getById(int orderId);
}
