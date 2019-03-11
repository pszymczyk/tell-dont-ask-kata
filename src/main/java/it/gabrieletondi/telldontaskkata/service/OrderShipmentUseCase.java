package it.gabrieletondi.telldontaskkata.service;

import it.gabrieletondi.telldontaskkata.entity.Order;
import it.gabrieletondi.telldontaskkata.entity.OrderStatus;
import it.gabrieletondi.telldontaskkata.repository.OrderRepository;

import static it.gabrieletondi.telldontaskkata.entity.OrderStatus.CREATED;
import static it.gabrieletondi.telldontaskkata.entity.OrderStatus.REJECTED;
import static it.gabrieletondi.telldontaskkata.entity.OrderStatus.SHIPPED;

public class OrderShipmentUseCase {
    private final OrderRepository orderRepository;
    private final ShipmentService shipmentService;

    public OrderShipmentUseCase(OrderRepository orderRepository, ShipmentService shipmentService) {
        this.orderRepository = orderRepository;
        this.shipmentService = shipmentService;
    }

    public void run(OrderShipmentRequest request) {
        final Order order = orderRepository.getById(request.getOrderId());

        if (order.getStatus().equals(CREATED) || order.getStatus().equals(REJECTED)) {
            throw new OrderCannotBeShippedException();
        }

        if (order.getStatus().equals(SHIPPED)) {
            throw new OrderCannotBeShippedTwiceException();
        }

        shipmentService.ship(order);

        order.setStatus(OrderStatus.SHIPPED);
        orderRepository.save(order);
    }
}
