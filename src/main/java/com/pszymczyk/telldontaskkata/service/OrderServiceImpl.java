package com.pszymczyk.telldontaskkata.service;

import com.pszymczyk.telldontaskkata.repository.OrderRepository;
import com.pszymczyk.telldontaskkata.repository.ProductCatalog;
import com.pszymczyk.telldontaskkata.util.ShipmentServiceUtil;

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
        new ApproveOrderFeature(orderRepository).invoke(request);
    }

    @Override
    public void createOrder(SellItemsRequest request) {
        new CreateOrderFeature(productCatalog, orderRepository).invoke(request);
    }

    @Override
    public void shipOrder(OrderShipmentRequest request) {
        new ShipOrderFeature(orderRepository, shipmentServiceUtil).ship(request);
    }
}
