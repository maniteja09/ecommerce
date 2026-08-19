package com.datalcott.ecommerce;

import com.datalcott.ecommerce.entity.Order;
import com.datalcott.ecommerce.entity.User;
import com.datalcott.ecommerce.repository.OrderRepository;
import com.datalcott.ecommerce.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private User user;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setTotalAmount(1000.0);
        order.setStatus("PLACED");
        order.setPaymentStatus("PAID");
    }

    @Test
    void saveOrder_shouldReturnSavedOrder() {

        when(orderRepository.save(order))
                .thenReturn(order);

        Order result = orderService.saveOrder(order);

        assertNotNull(result);
        assertEquals(order, result);

        verify(orderRepository)
                .save(order);
    }

    @Test
    void getAllOrders_shouldReturnAllOrders() {

        when(orderRepository.findAll())
                .thenReturn(List.of(order));

        List<Order> result =
                orderService.getAllOrders();

        assertEquals(1, result.size());
        assertEquals(order, result.get(0));

        verify(orderRepository)
                .findAll();
    }

    @Test
    void getOrdersByUser_shouldReturnUserOrders() {

        when(orderRepository.findByUser(user))
                .thenReturn(List.of(order));

        List<Order> result =
                orderService.getOrdersByUser(user);

        assertEquals(1, result.size());
        assertEquals(order, result.get(0));

        verify(orderRepository)
                .findByUser(user);
    }

    @Test
    void getOrderById_shouldReturnOrder() {

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        Order result =
                orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(order, result);

        verify(orderRepository)
                .findById(1L);
    }

    @Test
    void getOrderById_shouldReturnNullWhenNotFound() {

        when(orderRepository.findById(1L))
                .thenReturn(Optional.empty());

        Order result =
                orderService.getOrderById(1L);

        assertNull(result);

        verify(orderRepository)
                .findById(1L);
    }

    @Test
    void getOrderByIdForUser_shouldReturnOrder() {

        when(orderRepository.findByIdWithOrderItems(1L))
                .thenReturn(Optional.of(order));

        Order result =
                orderService.getOrderByIdForUser(1L, user);

        assertNotNull(result);
        assertEquals(order, result);

        verify(orderRepository)
                .findByIdWithOrderItems(1L);
    }

    @Test
    void getOrderByIdForUser_shouldThrowWhenOrderNotFound() {

        when(orderRepository.findByIdWithOrderItems(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> orderService.getOrderByIdForUser(1L, user)
                );

        assertEquals(
                "Order not found",
                exception.getMessage()
        );
    }

    @Test
    void getOrderByIdForUser_shouldThrowWhenUnauthorized() {

        User anotherUser = new User();
        anotherUser.setId(2L);

        when(orderRepository.findByIdWithOrderItems(1L))
                .thenReturn(Optional.of(order));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> orderService.getOrderByIdForUser(
                                1L,
                                anotherUser
                        )
                );

        assertEquals(
                "You are not authorized to view this order",
                exception.getMessage()
        );
    }

    @Test
    void deleteOrder_shouldDeleteOrder() {

        doNothing()
                .when(orderRepository)
                .deleteById(1L);

        orderService.deleteOrder(1L);

        verify(orderRepository)
                .deleteById(1L);
    }

    @Test
    void updateOrderStatus_shouldUpdateStatus() {

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        orderService.updateOrderStatus(
                1L,
                "SHIPPED"
        );

        assertEquals(
                "SHIPPED",
                order.getStatus()
        );

        verify(orderRepository)
                .findById(1L);

        verify(orderRepository)
                .save(order);
    }

    @Test
    void updateOrderStatus_shouldThrowWhenOrderNotFound() {

        when(orderRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> orderService.updateOrderStatus(
                                1L,
                                "SHIPPED"
                        )
                );

        assertEquals(
                "Order not found",
                exception.getMessage()
        );

        verify(orderRepository)
                .findById(1L);

        verify(orderRepository, never())
                .save(any(Order.class));
    }
}