package com.pszymczyk.telldontaskkata.service;

import com.pszymczyk.telldontaskkata.repository.OrderRepository;
import com.pszymczyk.telldontaskkata.repository.ProductCatalog;
import com.pszymczyk.telldontaskkata.util.ShipmentServiceUtil;

public class OrderService {

    private final ApproveOrderFeature approveOrderFeature;
    private final CreateOrderFeature createOrderFeature;
    private final ShipOrderFeature shipOrderFeature;

    public OrderService(OrderRepository orderRepository, ProductCatalog productCatalog, ShipmentServiceUtil shipmentServiceUtil) {
        this.approveOrderFeature = new ApproveOrderFeature(orderRepository);
        this.createOrderFeature = new CreateOrderFeature(productCatalog, orderRepository);
        this.shipOrderFeature = new ShipOrderFeature(orderRepository, shipmentServiceUtil);
    }

    public void approveOrder(OrderApprovalRequest request) {
        approveOrderFeature.approve(request);
    }

    public void createOrder(SellItemsRequest request) {
        createOrderFeature.create(request);
    }

    public void shipOrder(OrderShipmentRequest request) {
        shipOrderFeature.ship(request);
    }
}
