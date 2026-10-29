package com.nimbleways.springboilerplate.services.implementations.strategies;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ExpirableProductStrategyTest {

    private static final LocalDate PROCESSING_DATE =
            LocalDate.of(2026, 6, 15);

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ExpirableProductStrategy strategy;

    @Test
    void shouldDecreaseStock_whenProductIsNotExpired() {
        Product product = expirableProduct(
                2,
                LocalDate.of(2026, 6, 20)
        );

        strategy.process(product, PROCESSING_DATE);

        assertEquals(1, product.getAvailable());
        verify(productRepository).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldNotifyExpiration_whenProductIsExpired() {
        Product product = expirableProduct(
                2,
                LocalDate.of(2026, 6, 14)
        );

        strategy.process(product, PROCESSING_DATE);

        assertEquals(0, product.getAvailable());
        verify(productRepository).save(product);
        verify(notificationService)
                .sendExpirationNotification(
                        "Milk",
                        LocalDate.of(2026, 6, 14)
                );
    }

    @Test
    void shouldConsiderProductExpired_whenExpiryDateIsToday() {
        Product product = expirableProduct(
                2,
                PROCESSING_DATE
        );

        strategy.process(product, PROCESSING_DATE);

        assertEquals(0, product.getAvailable());
        verify(notificationService)
                .sendExpirationNotification(
                        "Milk",
                        PROCESSING_DATE
                );
    }

    private Product expirableProduct(
            int available,
            LocalDate expiryDate) {
        return new Product(
                null,
                5,
                available,
                ProductType.EXPIRABLE.name(),
                "Milk",
                expiryDate,
                null,
                null
        );
    }
}
