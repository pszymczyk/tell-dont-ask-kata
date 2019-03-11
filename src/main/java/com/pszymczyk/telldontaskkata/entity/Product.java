package com.pszymczyk.telldontaskkata.entity;

import java.math.BigDecimal;

import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_UP;

public class Product {

    private final String name;
    private final BigDecimal price;
    private final Category category;

    public Product(String name, BigDecimal price, Category category) {
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    BigDecimal taxedAmount(int quantity) {
        BigDecimal unitaryTaxedAmount = unitaryTaxedAmount(getUnitaryTax());
        return unitaryTaxedAmount.multiply(valueOf(quantity)).setScale(2, HALF_UP);
    }

    BigDecimal taxAmount(int quantity) {
        return getUnitaryTax().multiply(valueOf(quantity));
    }

    private BigDecimal unitaryTaxedAmount(BigDecimal unitaryTax) {
        return price.add(unitaryTax).setScale(2, HALF_UP);
    }

    private BigDecimal getUnitaryTax() {
        return price
                .divide(valueOf(100))
                .multiply(category.getTaxPercentage())
                .setScale(2, HALF_UP);
    }
}
