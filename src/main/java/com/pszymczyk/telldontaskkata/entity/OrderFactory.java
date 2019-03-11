package com.pszymczyk.telldontaskkata.entity;

import java.math.BigDecimal;
import java.util.ArrayList;

public class OrderFactory {

    public static Order create() {
        return new Order(new BigDecimal("0.00"),
                "EUR",
                new ArrayList<>(),
                new BigDecimal("0.00"),
                OrderStatus.CREATED);
    }
}
