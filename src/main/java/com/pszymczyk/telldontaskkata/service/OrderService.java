package com.pszymczyk.telldontaskkata.service;

public interface OrderService {

    void approveOrder(OrderApprovalRequest request);

    void createOrder(SellItemsRequest sellItemRequest);

    void shipOrder(OrderShipmentRequest request);
}
