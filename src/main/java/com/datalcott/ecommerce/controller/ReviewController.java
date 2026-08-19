package com.datalcott.ecommerce.controller;

import com.datalcott.ecommerce.entity.Product;
import com.datalcott.ecommerce.entity.Review;
import com.datalcott.ecommerce.entity.User;
import com.datalcott.ecommerce.service.ProductService;
import com.datalcott.ecommerce.service.ReviewService;
import com.datalcott.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class ReviewController {

    private final ReviewService reviewService;
    private final ProductService productService;
    private final UserService userService;

    public ReviewController(
            ReviewService reviewService,
            ProductService productService,
            UserService userService) {

        this.reviewService = reviewService;
        this.productService = productService;
        this.userService = userService;
    }


    @PostMapping("/reviews/add/{productId}")
    public String addReview(
            @PathVariable Long productId,
            @Valid @ModelAttribute("review") Review review,
            BindingResult bindingResult,
            Authentication authentication,
            Model model) {

        Product product =
                productService.getProductById(productId);

        if (product == null) {
            throw new RuntimeException("Product not found");
        }


        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "product",
                    product
            );

            model.addAttribute(
                    "reviews",
                    reviewService.getProductReviews(product)
            );

            return "product-details";
        }


        User user = userService
                .getUserByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));


        review.setUser(user);
        review.setProduct(product);

        reviewService.saveReview(review);

        return "redirect:/products/" + productId;
    }
}