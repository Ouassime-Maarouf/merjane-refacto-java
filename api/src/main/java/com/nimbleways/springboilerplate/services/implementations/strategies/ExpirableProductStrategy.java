package com.nimbleways.springboilerplate.services.implementations.strategies;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ExpirableProductStrategy implements ProductProcessingStrategy {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    public ExpirableProductStrategy(
            ProductRepository productRepository,
            NotificationService notificationService) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }

    @Override
    public ProductType supportedType() {
        return ProductType.EXPIRABLE;
    }

    @Override
    public void process(Product product, LocalDate processingDate) {
        if (product.getAvailable() > 0
                && product.getExpiryDate().isAfter(processingDate)) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
            return;
        }

        notificationService.sendExpirationNotification(
                product.getName(),
                product.getExpiryDate()
        );

        product.setAvailable(0);
        productRepository.save(product);
    }
}
