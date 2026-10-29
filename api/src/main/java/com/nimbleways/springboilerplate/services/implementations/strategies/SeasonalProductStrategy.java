package com.nimbleways.springboilerplate.services.implementations.strategies;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SeasonalProductStrategy implements ProductProcessingStrategy {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    public SeasonalProductStrategy(
            ProductRepository productRepository,
            NotificationService notificationService) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }

    @Override
    public ProductType supportedType() {
        return ProductType.SEASONAL;
    }

    @Override
    public void process(Product product, LocalDate processingDate) {
        if (isAvailableDuringSeason(product, processingDate)) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
            return;
        }

        if (processingDate.plusDays(product.getLeadTime())
                .isAfter(product.getSeasonEndDate())) {
            notificationService.sendOutOfStockNotification(product.getName());
            product.setAvailable(0);
            productRepository.save(product);
            return;
        }

        if (product.getSeasonStartDate().isAfter(processingDate)) {
            notificationService.sendOutOfStockNotification(product.getName());
            productRepository.save(product);
            return;
        }

        productRepository.save(product);
        notificationService.sendDelayNotification(
                product.getLeadTime(),
                product.getName()
        );
    }

    private boolean isAvailableDuringSeason(
            Product product,
            LocalDate processingDate) {
        return processingDate.isAfter(product.getSeasonStartDate())
                && processingDate.isBefore(product.getSeasonEndDate())
                && product.getAvailable() > 0;
    }
}
