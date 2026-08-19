package com.datalcott.ecommerce.controller;

import com.datalcott.ecommerce.entity.Cart;
import com.datalcott.ecommerce.service.CartService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }


    @GetMapping("/cart")
    public String viewCart(Authentication authentication,
                           Model model) {

        String email = authentication.getName();

        Cart cart = cartService.getOrCreateCart(email);

        var cartItems = cartService.getCartItems(cart.getId());

        double total = 0;

        for (var item : cartItems) {

            total += item.getProduct().getPrice()
                    * item.getQuantity();
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);

        return "cart";
    }


    @GetMapping("/cart/new")
    public String showCartForm(Model model) {

        model.addAttribute("cart", new Cart());

        return "cart-form";
    }


    @PostMapping("/cart/save")
    public String saveCart(@ModelAttribute Cart cart) {

        cartService.saveCart(cart);

        return "redirect:/cart";
    }


    @PostMapping("/cart/add/{productId}")
    public String addToCart(
            @PathVariable Long productId,
            @RequestParam int quantity,
            Authentication authentication) {

        String email = authentication.getName();

        cartService.addToCart(
                email,
                productId,
                quantity
        );

        return "redirect:/cart";
    }


    @PostMapping("/cart/remove/{cartItemId}")
    public String removeFromCart(
            @PathVariable Long cartItemId,
            Authentication authentication) {

        String email = authentication.getName();

        cartService.removeFromCart(
                email,
                cartItemId
        );

        return "redirect:/cart";
    }


    @PostMapping("/cart/update/{cartItemId}")
    public String updateQuantity(
            @PathVariable Long cartItemId,
            @RequestParam int quantity,
            Authentication authentication) {

        String email = authentication.getName();

        cartService.updateQuantity(
                email,
                cartItemId,
                quantity
        );

        return "redirect:/cart";
    }
}