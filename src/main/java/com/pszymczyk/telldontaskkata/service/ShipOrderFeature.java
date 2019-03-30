package com.pszymczyk.telldontaskkata.service;

import com.pszymczyk.telldontaskkata.entity.Order;
import com.pszymczyk.telldontaskkata.entity.OrderStatus;
import com.pszymczyk.telldontaskkata.repository.OrderRepository;
import com.pszymczyk.telldontaskkata.util.ShipmentServiceUtil;

import static com.pszymczyk.telldontaskkata.entity.OrderStatus.CREATED;
import static com.pszymczyk.telldontaskkata.entity.OrderStatus.REJECTED;
import static com.pszymczyk.telldontaskkata.entity.OrderStatus.SHIPPED;

class ShipOrderFeature {

    private final OrderRepository orderRepository;
    private final ShipmentServiceUtil shipmentServiceUtil;

    ShipOrderFeature(OrderRepository orderRepository, ShipmentServiceUtil shipmentServiceUtil) {
        this.orderRepository = orderRepository;
        this.shipmentServiceUtil = shipmentServiceUtil;
    }

    void invoke(OrderShipmentRequest request) {
        final Order order = this.orderRepository.getById(request.getOrderId());

        if (order.getStatus().equals(CREATED) || order.getStatus().equals(REJECTED)) {
            throw new OrderCannotBeShippedException();
        }

        if (order.getStatus().equals(SHIPPED)) {
            throw new OrderCannotBeShippedTwiceException();
        }

        this.shipmentServiceUtil.ship(order);

        order.setStatus(OrderStatus.SHIPPED);
        this.orderRepository.save(order);
    }
}
