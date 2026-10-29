package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.services.implementations.strategies.ProductProcessingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductServiceTest {

    private ProductProcessingStrategy normalStrategy;
    private ProductProcessingStrategy seasonalStrategy;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        normalStrategy =
                mock(ProductProcessingStrategy.class);
        seasonalStrategy =
                mock(ProductProcessingStrategy.class);

        when(normalStrategy.supportedType())
                .thenReturn(ProductType.NORMAL);
        when(seasonalStrategy.supportedType())
                .thenReturn(ProductType.SEASONAL);

        productService = new ProductService(
                List.of(normalStrategy, seasonalStrategy)
        );
    }

    @Test
    void shouldUseMatchingStrategy() {
        LocalDate processingDate =
                LocalDate.of(2026, 6, 15);
        Product product = new Product();
        product.setType(ProductType.NORMAL.name());

        productService.processProduct(
                product,
                processingDate
        );

        verify(normalStrategy)
                .process(product, processingDate);
        verify(seasonalStrategy, never())
                .process(product, processingDate);
    }

    @Test
    void shouldIgnoreUnknownProductType() {
        LocalDate processingDate =
                LocalDate.of(2026, 6, 15);
        Product product = new Product();
        product.setType("UNKNOWN");

        productService.processProduct(
                product,
                processingDate
        );

        verify(normalStrategy, never())
                .process(product, processingDate);
        verify(seasonalStrategy, never())
                .process(product, processingDate);
    }
}
