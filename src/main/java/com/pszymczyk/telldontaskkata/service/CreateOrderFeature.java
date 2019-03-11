package com.pszymczyk.telldontaskkata.service;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.pszymczyk.telldontaskkata.entity.Order;
import com.pszymczyk.telldontaskkata.entity.OrderFactory;
import com.pszymczyk.telldontaskkata.entity.OrderItem;
import com.pszymczyk.telldontaskkata.entity.OrderStatus;
import com.pszymczyk.telldontaskkata.entity.Product;
import com.pszymczyk.telldontaskkata.repository.OrderRepository;
import com.pszymczyk.telldontaskkata.repository.ProductCatalog;

import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_UP;

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

        orderRepository.save(order);
    }
}
