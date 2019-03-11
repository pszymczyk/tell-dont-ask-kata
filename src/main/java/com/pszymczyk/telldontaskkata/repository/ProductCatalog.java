package com.pszymczyk.telldontaskkata.repository;

import com.pszymczyk.telldontaskkata.entity.Product;

public interface ProductCatalog {
    Product getByName(String name);
}
