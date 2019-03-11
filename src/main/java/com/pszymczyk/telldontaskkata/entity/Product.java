package com.pszymczyk.telldontaskkata.entity;

import java.math.BigDecimal;

import com.pszymczyk.telldontaskkata.service.SellItemRequest;

import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_UP;

public class Product {

    private String name;
    private BigDecimal price;
    private Category category;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    private BigDecimal getUnitaryTax() {
        return price
                .divide(valueOf(100))
                .multiply(category.getTaxPercentage())
                .setScale(2, HALF_UP);
    }

    private BigDecimal unitaryTaxedAmount(BigDecimal unitaryTax) {
        return price.add(unitaryTax).setScale(2, HALF_UP);
    }

    public BigDecimal taxedAmount(int quantity) {
        BigDecimal unitaryTaxedAmount = unitaryTaxedAmount(getUnitaryTax());
        return unitaryTaxedAmount.multiply(valueOf(quantity)).setScale(2, HALF_UP);
    }

    public BigDecimal taxAmount(int quantity) {
        return getUnitaryTax().multiply(valueOf(quantity));
    }
}
