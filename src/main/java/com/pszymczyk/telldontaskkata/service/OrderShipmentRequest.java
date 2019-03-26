package com.pszymczyk.telldontaskkata.service;

import java.util.UUID;

public class OrderShipmentRequest {
    private UUID orderId;

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getOrderId() {
        return orderId;
    }
}
