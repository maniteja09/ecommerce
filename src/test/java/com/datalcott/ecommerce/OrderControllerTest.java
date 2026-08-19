package com.datalcott.ecommerce;

import com.datalcott.ecommerce.controller.OrderController;
import com.datalcott.ecommerce.entity.Order;
import com.datalcott.ecommerce.entity.OrderItem;
import com.datalcott.ecommerce.entity.User;
import com.datalcott.ecommerce.repository.OrderItemRepository;
import com.datalcott.ecommerce.service.OrderService;
import com.datalcott.ecommerce.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private UserService userService;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;

    private User user;
    private Order order;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(orderController)
                .setViewResolvers((viewName, locale) -> {

                    if (viewName.startsWith("redirect:")) {
                        return new org.springframework.web.servlet.view.RedirectView(
                                viewName.substring("redirect:".length())
                        );
                    }

                    return (model, request, response) -> {
                    };
                })
                .build();

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

        orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrder(order);
        orderItem.setQuantity(2);
        orderItem.setPrice(500.0);
    }

    @Test
    void getUserOrders_shouldReturnOrdersPage()
            throws Exception {

        when(authentication.getName())
                .thenReturn("test@example.com");

        when(userService.getUserByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(orderService.getOrdersByUser(user))
                .thenReturn(List.of(order));

        mockMvc.perform(
                        get("/orders")
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"));

        verify(userService)
                .getUserByEmail("test@example.com");

        verify(orderService)
                .getOrdersByUser(user);
    }

    @Test
    void getOrderById_shouldReturnOrderDetailsPage()
            throws Exception {

        when(authentication.getName())
                .thenReturn("test@example.com");

        when(userService.getUserByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(orderService.getOrderByIdForUser(1L, user))
                .thenReturn(order);

        when(orderItemRepository.findByOrder(order))
                .thenReturn(List.of(orderItem));

        mockMvc.perform(
                        get("/orders/1")
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(view().name("order-details"))
                .andExpect(model().attributeExists("order"))
                .andExpect(model().attributeExists("orderItems"));

        verify(orderService)
                .getOrderByIdForUser(1L, user);

        verify(orderItemRepository)
                .findByOrder(order);
    }

    @Test
    void adminOrders_shouldReturnAdminOrdersPage()
            throws Exception {

        when(orderService.getAllOrders())
                .thenReturn(List.of(order));

        mockMvc.perform(
                        get("/admin/orders")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("admin-orders"))
                .andExpect(model().attributeExists("orders"));

        verify(orderService)
                .getAllOrders();
    }

    @Test
    void updateOrderStatus_shouldRedirectToAdminOrders()
            throws Exception {

        doNothing()
                .when(orderService)
                .updateOrderStatus(1L, "SHIPPED");

        mockMvc.perform(
                        post("/admin/orders/update-status")
                                .param("orderId", "1")
                                .param("status", "SHIPPED")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(
                        redirectedUrl("/admin/orders")
                );

        verify(orderService)
                .updateOrderStatus(1L, "SHIPPED");
    }

    @Test
    void getUserOrders_shouldThrowWhenUserNotFound()
            throws Exception {

        when(authentication.getName())
                .thenReturn("test@example.com");

        when(userService.getUserByEmail("test@example.com"))
                .thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
                Exception.class,
                () -> mockMvc.perform(
                        get("/orders")
                                .principal(authentication)
                )
        );

        verify(userService)
                .getUserByEmail("test@example.com");
    }

    @Test
    void getOrderById_shouldThrowWhenUserNotFound()
            throws Exception {

        when(authentication.getName())
                .thenReturn("test@example.com");

        when(userService.getUserByEmail("test@example.com"))
                .thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
                Exception.class,
                () -> mockMvc.perform(
                        get("/orders/1")
                                .principal(authentication)
                )
        );

        verify(userService)
                .getUserByEmail("test@example.com");
    }
}