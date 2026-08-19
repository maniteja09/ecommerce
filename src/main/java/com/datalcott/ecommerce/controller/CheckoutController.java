package com.datalcott.ecommerce.controller;

import com.datalcott.ecommerce.entity.CartItem;
import com.datalcott.ecommerce.entity.Order;
import com.datalcott.ecommerce.entity.OrderItem;
import com.datalcott.ecommerce.entity.User;
import com.datalcott.ecommerce.repository.OrderItemRepository;
import com.datalcott.ecommerce.repository.OrderRepository;
import com.datalcott.ecommerce.repository.UserRepository;
import com.datalcott.ecommerce.service.CartService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class CheckoutController {

    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;

    public CheckoutController(
            CartService cartService,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            UserRepository userRepository) {

        this.cartService = cartService;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
    }


    @GetMapping("/checkout")
    public String checkout(
            Authentication authentication,
            Model model) {

        String email = authentication.getName();

        List<CartItem> cartItems =
                cartService.getCartItemsByEmail(email);

        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        double total = 0;

        for (CartItem item : cartItems) {

            total += item.getProduct().getPrice()
                    * item.getQuantity();
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);

        return "checkout";
    }


    @PostMapping("/checkout/place-order")
    public String placeOrder(
            @RequestParam(required = false) String paymentMethod,
            Authentication authentication,
            Model model) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));


        List<CartItem> cartItems =
                cartService.getCartItemsByEmail(email);


        // Check empty cart

        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }


        // Check payment method

        if (paymentMethod == null
                || !(paymentMethod.equals("CARD")
                || paymentMethod.equals("UPI")
                || paymentMethod.equals("COD"))) {

            return showCheckoutError(
                    model,
                    cartItems,
                    "Please select a valid payment method."
            );
        }


        double total = 0;


        // Validate cart items

        for (CartItem item : cartItems) {

            // Quantity validation

            if (item.getQuantity() <= 0) {

                return showCheckoutError(
                        model,
                        cartItems,
                        "Product quantity must be greater than 0."
                );
            }


            // Stock validation

            if (item.getQuantity()
                    > item.getProduct().getStock()) {

                return showCheckoutError(
                        model,
                        cartItems,
                        "Not enough stock available for "
                                + item.getProduct().getName()
                );
            }


            total += item.getProduct().getPrice()
                    * item.getQuantity();
        }


        // Total validation

        if (total <= 0) {

            return showCheckoutError(
                    model,
                    cartItems,
                    "Order total must be greater than 0."
            );
        }


        // Create Order

        Order order = new Order();

        order.setUser(user);
        order.setTotalAmount(total);
        order.setStatus("PLACED");
        order.setPaymentStatus(
                paymentMethod.equals("COD")
                        ? "PENDING"
                        : "PAID"
        );
        order.setOrderDate(LocalDateTime.now());


        Order savedOrder =
                orderRepository.save(order);


        // Create Order Items

        for (CartItem cartItem : cartItems) {

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(savedOrder);

            orderItem.setProduct(
                    cartItem.getProduct()
            );

            orderItem.setQuantity(
                    cartItem.getQuantity()
            );

            orderItem.setPrice(
                    cartItem.getProduct().getPrice()
            );

            orderItemRepository.save(orderItem);


            // Reduce product stock

            cartItem.getProduct().setStock(
                    cartItem.getProduct().getStock()
                            - cartItem.getQuantity()
            );
        }


        // Clear current user's cart

        cartService.clearCartByEmail(email);


        return "redirect:/orders";
    }


    private String showCheckoutError(
            Model model,
            List<CartItem> cartItems,
            String errorMessage) {

        double total = 0;

        for (CartItem item : cartItems) {

            total += item.getProduct().getPrice()
                    * item.getQuantity();
        }

        model.addAttribute(
                "cartItems",
                cartItems
        );

        model.addAttribute(
                "total",
                total
        );

        model.addAttribute(
                "errorMessage",
                errorMessage
        );

        return "checkout";
    }
}