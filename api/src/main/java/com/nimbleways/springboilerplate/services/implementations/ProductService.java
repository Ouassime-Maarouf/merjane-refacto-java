package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.services.implementations.strategies.ProductProcessingStrategy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProductService {

    private final List<ProductProcessingStrategy> strategies;

    public ProductService(List<ProductProcessingStrategy> strategies) {
        this.strategies = strategies;
    }

    public void processProduct(Product product) {
        processProduct(product, LocalDate.now());
    }

    public void processProduct(
            Product product,
            LocalDate processingDate) {
        strategies.stream()
                .filter(strategy ->
                        strategy.supportedType().name()
                                .equals(product.getType()))
                .findFirst()
                .ifPresent(strategy ->
                        strategy.process(product, processingDate));
    }
}
