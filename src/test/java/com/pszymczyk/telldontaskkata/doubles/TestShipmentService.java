package com.pszymczyk.telldontaskkata.doubles;

import com.pszymczyk.telldontaskkata.entity.Order;
import com.pszymczyk.telldontaskkata.util.ShipmentServiceUtil;

public class TestShipmentService implements ShipmentServiceUtil {
    private Order shippedOrder = null;

    public Order getShippedOrder() {
        return shippedOrder;
    }

    @Override
    public void ship(Order order) {
        this.shippedOrder = order;
    }
}
