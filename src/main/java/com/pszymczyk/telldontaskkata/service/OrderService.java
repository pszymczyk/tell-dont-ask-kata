package com.pszymczyk.telldontaskkata.service;

import java.util.List;

public interface OrderService {

    List<OrderDTO> findAllApprovedOrders();

    void approveOrder(OrderApprovalRequest request);

    void createOrder(SellItemsRequest sellItemRequest);

    void shipOrder(OrderShipmentRequest request);
}
