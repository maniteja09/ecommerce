package com.datalcott.ecommerce.controller;

import com.datalcott.ecommerce.entity.Order;
import com.datalcott.ecommerce.entity.OrderItem;
import com.datalcott.ecommerce.entity.User;
import com.datalcott.ecommerce.repository.OrderItemRepository;
import com.datalcott.ecommerce.service.OrderService;
import com.datalcott.ecommerce.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final OrderItemRepository orderItemRepository;


    public OrderController(
            OrderService orderService,
            UserService userService,
            OrderItemRepository orderItemRepository) {

        this.orderService = orderService;
        this.userService = userService;
        this.orderItemRepository = orderItemRepository;
    }


    // =========================
    // USER ORDERS
    // =========================

    @GetMapping("/orders")
    public String getUserOrders(
            Authentication authentication,
            Model model) {

        String email = authentication.getName();

        User user = userService
                .getUserByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        model.addAttribute(
                "orders",
                orderService.getOrdersByUser(user)
        );

        return "orders";
    }


    // =========================
    // VIEW USER ORDER DETAILS
    // =========================

    @GetMapping("/orders/{id}")
    public String getOrderById(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {

        String email = authentication.getName();

        User user = userService
                .getUserByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Order order =
                orderService.getOrderByIdForUser(id, user);


        // Get ordered products separately
        List<OrderItem> orderItems =
                orderItemRepository.findByOrder(order);


        model.addAttribute(
                "order",
                order
        );

        model.addAttribute(
                "orderItems",
                orderItems
        );


        return "order-details";
    }


    // =========================
    // ADMIN - ALL ORDERS
    // =========================

    @GetMapping("/admin/orders")
    public String adminOrders(Model model) {

        model.addAttribute(
                "orders",
                orderService.getAllOrders()
        );

        return "admin-orders";
    }


    // =========================
    // ADMIN - UPDATE STATUS
    // =========================

    @PostMapping("/admin/orders/update-status")
    public String updateOrderStatus(
            @RequestParam Long orderId,
            @RequestParam String status) {

        orderService.updateOrderStatus(
                orderId,
                status
        );

        return "redirect:/admin/orders";
    }
}