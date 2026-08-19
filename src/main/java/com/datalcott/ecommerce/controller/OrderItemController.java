package com.datalcott.ecommerce.controller;

import com.datalcott.ecommerce.entity.OrderItem;
import com.datalcott.ecommerce.service.OrderItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class OrderItemController {

    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @GetMapping("/order-items")
    public String getAllOrderItems(Model model) {
        model.addAttribute("orderItems", orderItemService.getAllOrderItems());
        return "order-items";
    }

    @PostMapping("/order-items/save")
    public String saveOrderItem(@ModelAttribute OrderItem orderItem) {
        orderItemService.saveOrderItem(orderItem);
        return "redirect:/orders";
    }
}
