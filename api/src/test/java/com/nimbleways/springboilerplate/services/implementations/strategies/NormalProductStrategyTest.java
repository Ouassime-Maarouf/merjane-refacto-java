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
class NormalProductStrategyTest {

    private static final LocalDate PROCESSING_DATE =
            LocalDate.of(2026, 6, 15);

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NormalProductStrategy strategy;

    @Test
    void shouldDecreaseStock_whenProductIsAvailable() {
        Product product = normalProduct(2, 5);

        strategy.process(product, PROCESSING_DATE);

        assertEquals(1, product.getAvailable());
        verify(productRepository).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldNotifyDelay_whenProductIsOutOfStock() {
        Product product = normalProduct(0, 5);

        strategy.process(product, PROCESSING_DATE);

        assertEquals(0, product.getAvailable());
        verify(productRepository).save(product);
        verify(notificationService)
                .sendDelayNotification(5, "Cable");
    }

    @Test
    void shouldDoNothing_whenStockAndLeadTimeAreZero() {
        Product product = normalProduct(0, 0);

        strategy.process(product, PROCESSING_DATE);

        verifyNoInteractions(
                productRepository,
                notificationService
        );
    }

    private Product normalProduct(
            int available,
            int leadTime) {
        return new Product(
                null,
                leadTime,
                available,
                ProductType.NORMAL.name(),
                "Cable",
                null,
                null,
                null
        );
    }
}
