package com.nimbleways.springboilerplate.services.implementations.strategies;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class NormalProductStrategy implements ProductProcessingStrategy {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    public NormalProductStrategy(
            ProductRepository productRepository,
            NotificationService notificationService) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }

    @Override
    public ProductType supportedType() {
        return ProductType.NORMAL;
    }

    @Override
    public void process(Product product, LocalDate processingDate) {
        if (product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
            return;
        }

        if (product.getLeadTime() > 0) {
            productRepository.save(product);
            notificationService.sendDelayNotification(
                    product.getLeadTime(),
                    product.getName()
            );
        }
    }
}
