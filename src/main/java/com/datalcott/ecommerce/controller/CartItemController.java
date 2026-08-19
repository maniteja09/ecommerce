package com.datalcott.ecommerce.controller;

import com.datalcott.ecommerce.entity.CartItem;
import com.datalcott.ecommerce.service.CartItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CartItemController {

    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @GetMapping("/cart-items")
    public String getAllCartItems(Model model) {
        model.addAttribute("cartItems", cartItemService.getAllCartItems());
        return "cart-items";
    }

    @PostMapping("/cart-items/save")
    public String saveCartItem(@ModelAttribute CartItem cartItem) {
        cartItemService.saveCartItem(cartItem);
        return "redirect:/cart";
    }
}
