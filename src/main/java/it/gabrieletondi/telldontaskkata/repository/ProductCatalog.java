package it.gabrieletondi.telldontaskkata.repository;

import it.gabrieletondi.telldontaskkata.entity.Product;

public interface ProductCatalog {
    Product getByName(String name);
}
