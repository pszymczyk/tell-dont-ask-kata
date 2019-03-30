package com.pszymczyk.telldontaskkata.service;

import java.util.List;

import com.pszymczyk.telldontaskkata.entity.Order;
import com.pszymczyk.telldontaskkata.entity.OrderStatus;
import com.pszymczyk.telldontaskkata.repository.OrderRepository;

@Deprecated
class LegacyApproveOrderFeature {

    private final OrderRepository orderRepository;

    public LegacyApproveOrderFeature(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    void invoke(OrderApprovalRequest request) {
        final Order order = this.orderRepository.getById(request.getOrderId());

        if (order.getStatus().equals(OrderStatus.SHIPPED)) {
            throw new ShippedOrdersCannotBeChangedException();
        }

        if (request.isApproved() && order.getStatus().equals(OrderStatus.REJECTED)) {
            throw new RejectedOrderCannotBeApprovedException();
        }

        if (!request.isApproved() && order.getStatus().equals(OrderStatus.APPROVED)) {
            throw new ApprovedOrderCannotBeRejectedException();
        }

        order.setStatus(request.isApproved() ? OrderStatus.APPROVED : OrderStatus.REJECTED);
        orderRepository.save(order);
    }

    public List<OrderDTO> findAllApprovedOrders() {
        return null;
    }
}
