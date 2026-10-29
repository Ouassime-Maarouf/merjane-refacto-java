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
class SeasonalProductStrategyTest {

    private static final LocalDate PROCESSING_DATE =
            LocalDate.of(2026, 6, 15);

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private SeasonalProductStrategy strategy;

    @Test
    void shouldDecreaseStock_whenProductIsInSeason() {
        Product product = seasonalProduct(
                2,
                5,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30)
        );

        strategy.process(product, PROCESSING_DATE);

        assertEquals(1, product.getAvailable());
        verify(productRepository).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldNotifyDelay_whenRestockingIsInSeason() {
        Product product = seasonalProduct(
                0,
                5,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30)
        );

        strategy.process(product, PROCESSING_DATE);

        verify(productRepository).save(product);
        verify(notificationService)
                .sendDelayNotification(5, "Watermelon");
    }

    @Test
    void shouldNotifyUnavailability_whenDelayExceedsSeasonEnd() {
        Product product = seasonalProduct(
                0,
                20,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30)
        );

        strategy.process(product, PROCESSING_DATE);

        assertEquals(0, product.getAvailable());
        verify(productRepository).save(product);
        verify(notificationService)
                .sendOutOfStockNotification("Watermelon");
    }

    @Test
    void shouldNotifyUnavailability_whenSeasonHasNotStarted() {
        Product product = seasonalProduct(
                2,
                5,
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 8, 1)
        );

        strategy.process(product, PROCESSING_DATE);

        assertEquals(2, product.getAvailable());
        verify(productRepository).save(product);
        verify(notificationService)
                .sendOutOfStockNotification("Watermelon");
    }

    private Product seasonalProduct(
            int available,
            int leadTime,
            LocalDate seasonStart,
            LocalDate seasonEnd) {
        return new Product(
                null,
                leadTime,
                available,
                ProductType.SEASONAL.name(),
                "Watermelon",
                null,
                seasonStart,
                seasonEnd
        );
    }
}
