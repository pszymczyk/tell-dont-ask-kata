package it.gabrieletondi.telldontaskkata.service;

public interface OrderService {

    void approveOrder(OrderApprovalRequest request);

    void createOrder(SellItemsRequest sellItemRequest);
}
