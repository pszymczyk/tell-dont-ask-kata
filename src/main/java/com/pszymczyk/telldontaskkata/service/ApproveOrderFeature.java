package com.pszymczyk.telldontaskkata.service;

import com.pszymczyk.telldontaskkata.entity.Order;
import com.pszymczyk.telldontaskkata.repository.OrderRepository;

class ApproveOrderFeature {

    private final OrderRepository orderRepository;

    ApproveOrderFeature(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    void approve(OrderApprovalRequest request) {
        final Order order = orderRepository.getById(request.getOrderId());

        if (order.isShipped()) {
            throw new ShippedOrdersCannotBeChangedException();
        }

        if (request.isApproved() && order.isRejected()) {
            throw new RejectedOrderCannotBeApprovedException();
        }

        if (!request.isApproved() && order.isApproved()) {
            throw new ApprovedOrderCannotBeRejectedException();
        }

        Order newOrder = order.apply(request);
        orderRepository.save(newOrder);
    }
}
