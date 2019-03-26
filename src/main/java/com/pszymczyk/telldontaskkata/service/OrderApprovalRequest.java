package com.pszymczyk.telldontaskkata.service;

import java.util.UUID;

public class OrderApprovalRequest {
    private UUID orderId;
    private boolean approved;

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isApproved() {
        return approved;
    }
}
