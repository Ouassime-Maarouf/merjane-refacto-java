package com.nimbleways.springboilerplate.controllers;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @MockBean
    private NotificationService notificationService;

    @Test
    void shouldProcessOrderAndReturnItsId() throws Exception {
        Product product = new Product(
                null,
                5,
                2,
                ProductType.NORMAL.name(),
                "Cable",
                null,
                null,
                null
        );

        product = productRepository.save(product);

        Order order = new Order();
        order.setItems(Set.of(product));
        order = orderRepository.save(order);

        mockMvc.perform(post(
                        "/orders/{orderId}/processOrder",
                        order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(order.getId()));

        Product savedProduct = productRepository
                .findById(product.getId())
                .orElseThrow();

        assertEquals(1, savedProduct.getAvailable());
    }

    @Test
    void shouldReturnNotFound_whenOrderDoesNotExist()
            throws Exception {
        mockMvc.perform(post(
                        "/orders/{orderId}/processOrder",
                        999999L))
                .andExpect(status().isNotFound());
    }
}
