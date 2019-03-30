package com.pszymczyk.telldontaskkata.service;

import java.util.List;

public class OrderServiceImpl implements OrderService {

    private final ApproveOrderFeature approveOrderFeature;
    private final CreateOrderFeature createOrderFeature;
    private final ShipOrderFeature shipOrderFeature;

    public OrderServiceImpl(
            ApproveOrderFeature approveOrderFeature,
            CreateOrderFeature createOrderFeature,
            ShipOrderFeature shipOrderFeature) {
        this.approveOrderFeature = approveOrderFeature;
        this.createOrderFeature = createOrderFeature;
        this.shipOrderFeature = shipOrderFeature;
    }

    @Override
    public List<OrderDTO> findAllApprovedOrders() {
        return approveOrderFeature.findAllApprovedOrders();
    }

    @Override
    public void approveOrder(OrderApprovalRequest request) {
        approveOrderFeature.invoke(request);
    }

    @Override
    public void createOrder(SellItemsRequest request) {
        createOrderFeature.invoke(request);
    }

    @Override
    public void shipOrder(OrderShipmentRequest request) {
        shipOrderFeature.invoke(request);
    }
}
