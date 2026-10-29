package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.exceptions.OrderNotFoundException;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldProcessEveryProduct() {
        Product firstProduct = new Product();
        Product secondProduct = new Product();
        Order order = new Order(
                10L,
                Set.of(firstProduct, secondProduct)
        );

        when(orderRepository.findById(10L))
                .thenReturn(Optional.of(order));

        ProcessOrderResponse response =
                orderService.processOrder(10L);

        assertEquals(10L, response.id());
        verify(productService)
                .processProduct(firstProduct);
        verify(productService)
                .processProduct(secondProduct);
    }

    @Test
    void shouldThrowException_whenOrderDoesNotExist() {
        when(orderRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.processOrder(10L)
        );
    }
}
