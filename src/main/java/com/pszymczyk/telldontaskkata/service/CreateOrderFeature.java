package com.pszymczyk.telldontaskkata.service;

import java.math.BigDecimal;

import com.pszymczyk.telldontaskkata.entity.Order;
import com.pszymczyk.telldontaskkata.entity.OrderFactory;
import com.pszymczyk.telldontaskkata.entity.OrderItem;
import com.pszymczyk.telldontaskkata.entity.OrderItemFactory;
import com.pszymczyk.telldontaskkata.entity.Product;
import com.pszymczyk.telldontaskkata.repository.OrderRepository;
import com.pszymczyk.telldontaskkata.repository.ProductCatalog;

class CreateOrderFeature {

    private final ProductCatalog productCatalog;
    private final OrderRepository orderRepository;

    public CreateOrderFeature(ProductCatalog productCatalog, OrderRepository orderRepository) {
        this.productCatalog = productCatalog;
        this.orderRepository = orderRepository;
    }

    void create(SellItemsRequest request) {
        Order order = OrderFactory.create();

        for (SellItemRequest itemRequest : request.getRequests()) {
            Product product = productCatalog.getByName(itemRequest.getProductName());
            final BigDecimal taxedAmount = product.taxedAmount(itemRequest.getQuantity());
            final BigDecimal taxAmount = product.taxAmount(itemRequest.getQuantity());
            final OrderItem orderItem = OrderItemFactory.create(product, itemRequest.getQuantity());
            order.addItem(orderItem, taxedAmount, taxAmount);
        }

        orderRepository.save(order);
    }
}
