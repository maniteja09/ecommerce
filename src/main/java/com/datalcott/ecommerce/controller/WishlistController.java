package com.datalcott.ecommerce.controller;

import com.datalcott.ecommerce.entity.Product;
import com.datalcott.ecommerce.entity.User;
import com.datalcott.ecommerce.service.ProductService;
import com.datalcott.ecommerce.service.UserService;
import com.datalcott.ecommerce.service.WishlistService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserService userService;
    private final ProductService productService;

    public WishlistController(
            WishlistService wishlistService,
            UserService userService,
            ProductService productService) {

        this.wishlistService = wishlistService;
        this.userService = userService;
        this.productService = productService;
    }

    @PostMapping("/wishlist/add/{productId}")
    public String addToWishlist(
            @PathVariable Long productId,
            Authentication authentication) {

        User user = userService
                .getUserByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Product product =
                productService.getProductById(productId);

        wishlistService.addToWishlist(user, product);

        return "redirect:/wishlist";
    }

    @GetMapping("/wishlist")
    public String viewWishlist(
            Authentication authentication,
            Model model) {

        User user = userService
                .getUserByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        model.addAttribute(
                "wishlistItems",
                wishlistService.getWishlist(user)
        );

        return "wishlist";
    }

    @PostMapping("/wishlist/remove/{id}")
    public String removeFromWishlist(
            @PathVariable Long id,
            Authentication authentication) {

        User user = userService
                .getUserByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        wishlistService.removeFromWishlist(id, user);

        return "redirect:/wishlist";
    }
}