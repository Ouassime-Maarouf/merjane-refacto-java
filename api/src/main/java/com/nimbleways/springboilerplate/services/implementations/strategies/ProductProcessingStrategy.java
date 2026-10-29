package com.nimbleways.springboilerplate.services.implementations.strategies;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;

import java.time.LocalDate;

public interface ProductProcessingStrategy {

    ProductType supportedType();

    void process(Product product, LocalDate processingDate);
}
