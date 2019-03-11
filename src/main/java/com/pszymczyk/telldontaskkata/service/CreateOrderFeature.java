package com.pszymczyk.telldontaskkata.service;

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
            OrderItem orderItem = OrderItemFactory.create(product, itemRequest.getQuantity());
            order.addItem(orderItem);
        }

        orderRepository.save(order);
    }
}
