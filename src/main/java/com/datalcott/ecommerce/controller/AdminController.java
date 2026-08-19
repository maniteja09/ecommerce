package com.datalcott.ecommerce.controller;

import com.datalcott.ecommerce.service.CategoryService;
import com.datalcott.ecommerce.service.OrderService;
import com.datalcott.ecommerce.service.ProductService;
import com.datalcott.ecommerce.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final OrderService orderService;

    public AdminController(ProductService productService,
                           CategoryService categoryService,
                           UserService userService,
                           OrderService orderService) {

        this.productService = productService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/admin")
    public String adminDashboard(Model model) {

        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("orders", orderService.getAllOrders());

        return "admin";
    }
}
