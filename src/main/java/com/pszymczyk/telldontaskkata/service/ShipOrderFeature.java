package com.pszymczyk.telldontaskkata.service;

import com.pszymczyk.telldontaskkata.entity.Order;
import com.pszymczyk.telldontaskkata.repository.OrderRepository;
import com.pszymczyk.telldontaskkata.util.ShipmentServiceUtil;

class ShipOrderFeature {

    private final OrderRepository orderRepository;
    private final ShipmentServiceUtil shipmentServiceUtil;

    ShipOrderFeature(OrderRepository orderRepository, ShipmentServiceUtil shipmentServiceUtil) {
        this.orderRepository = orderRepository;
        this.shipmentServiceUtil = shipmentServiceUtil;
    }

    void ship(OrderShipmentRequest request) {
        final Order order = orderRepository.getById(request.getOrderId());

        if (order.isCreated() || order.isRejected()) {
            throw new OrderCannotBeShippedException();
        }

        if (order.isShipped()) {
            throw new OrderCannotBeShippedTwiceException();
        }

        shipmentServiceUtil.ship(order);

        Order shippedOrder = order.ship();
        orderRepository.save(shippedOrder);
    }
}
