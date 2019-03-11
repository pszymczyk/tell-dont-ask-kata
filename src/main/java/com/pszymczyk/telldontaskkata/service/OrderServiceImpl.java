package com.pszymczyk.telldontaskkata.service;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.pszymczyk.telldontaskkata.entity.Order;
import com.pszymczyk.telldontaskkata.entity.OrderItem;
import com.pszymczyk.telldontaskkata.entity.OrderStatus;
import com.pszymczyk.telldontaskkata.entity.Product;
import com.pszymczyk.telldontaskkata.repository.OrderRepository;
import com.pszymczyk.telldontaskkata.repository.ProductCatalog;
import com.pszymczyk.telldontaskkata.util.ShipmentServiceUtil;

import static com.pszymczyk.telldontaskkata.entity.OrderStatus.CREATED;
import static com.pszymczyk.telldontaskkata.entity.OrderStatus.REJECTED;
import static com.pszymczyk.telldontaskkata.entity.OrderStatus.SHIPPED;
import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_UP;

public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductCatalog productCatalog;
    private final ShipmentServiceUtil shipmentServiceUtil;

    public OrderServiceImpl(OrderRepository orderRepository, ProductCatalog productCatalog, ShipmentServiceUtil shipmentServiceUtil) {
        this.orderRepository = orderRepository;
        this.productCatalog = productCatalog;
        this.shipmentServiceUtil = shipmentServiceUtil;
    }

    @Override
    public void approveOrder(OrderApprovalRequest request) {
        final Order order = orderRepository.getById(request.getOrderId());

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

    @Override
    public void createOrder(SellItemsRequest request) {
        Order order = new Order();
        order.setStatus(OrderStatus.CREATED);
        order.setItems(new ArrayList<>());
        order.setCurrency("EUR");
        order.setTotal(new BigDecimal("0.00"));
        order.setTax(new BigDecimal("0.00"));

        for (SellItemRequest itemRequest : request.getRequests()) {
            Product product = productCatalog.getByName(itemRequest.getProductName());

            if (product == null) {
                throw new UnknownProductException();
            } else {
                final BigDecimal unitaryTax = product.getPrice()
                                                     .divide(valueOf(100))
                                                     .multiply(product.getCategory().getTaxPercentage())
                                                     .setScale(2, HALF_UP);
                final BigDecimal unitaryTaxedAmount = product.getPrice().add(unitaryTax).setScale(2, HALF_UP);
                final BigDecimal taxedAmount = unitaryTaxedAmount.multiply(valueOf(itemRequest.getQuantity()))
                                                                 .setScale(2, HALF_UP);
                final BigDecimal taxAmount = unitaryTax.multiply(valueOf(itemRequest.getQuantity()));

                final OrderItem orderItem = new OrderItem();
                orderItem.setProduct(product);
                orderItem.setQuantity(itemRequest.getQuantity());
                orderItem.setTax(taxAmount);
                orderItem.setTaxedAmount(taxedAmount);
                order.getItems().add(orderItem);

                order.setTotal(order.getTotal().add(taxedAmount));
                order.setTax(order.getTax().add(taxAmount));
            }
        }

        orderRepository.save(order);
    }

    @Override
    public void shipOrder(OrderShipmentRequest request) {
        final Order order = orderRepository.getById(request.getOrderId());

        if (order.getStatus().equals(CREATED) || order.getStatus().equals(REJECTED)) {
            throw new OrderCannotBeShippedException();
        }

        if (order.getStatus().equals(SHIPPED)) {
            throw new OrderCannotBeShippedTwiceException();
        }

        shipmentServiceUtil.ship(order);

        order.setStatus(OrderStatus.SHIPPED);
        orderRepository.save(order);
    }
}
