package com.datalcott.ecommerce;

import com.datalcott.ecommerce.controller.CheckoutController;
import com.datalcott.ecommerce.entity.CartItem;
import com.datalcott.ecommerce.entity.Order;
import com.datalcott.ecommerce.entity.Product;
import com.datalcott.ecommerce.entity.User;
import com.datalcott.ecommerce.repository.OrderItemRepository;
import com.datalcott.ecommerce.repository.OrderRepository;
import com.datalcott.ecommerce.repository.UserRepository;
import com.datalcott.ecommerce.service.CartService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CheckoutControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CartService cartService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CheckoutController checkoutController;


    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(checkoutController)
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
    }


    private CartItem createCartItem(
            int quantity,
            int stock,
            double price) {

        Product product = new Product();

        product.setId(1L);
        product.setName("Laptop");
        product.setDescription("Gaming Laptop");
        product.setPrice(price);
        product.setStock(stock);

        CartItem cartItem = new CartItem();

        cartItem.setId(1L);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);

        return cartItem;
    }


    @Test
    void checkout_shouldReturnCheckoutPage()
            throws Exception {

        CartItem cartItem =
                createCartItem(2, 10, 50000);

        when(authentication.getName())
                .thenReturn("test@example.com");

        when(cartService.getCartItemsByEmail("test@example.com"))
                .thenReturn(List.of(cartItem));

        mockMvc.perform(get("/checkout")
                        .principal(authentication))

                .andExpect(status().isOk())
                .andExpect(view().name("checkout"))
                .andExpect(model().attributeExists("cartItems"))
                .andExpect(model().attribute("total", 100000.0));

        verify(cartService)
                .getCartItemsByEmail("test@example.com");
    }


    @Test
    void checkout_shouldRedirectToCartWhenCartIsEmpty()
            throws Exception {

        when(authentication.getName())
                .thenReturn("test@example.com");

        when(cartService.getCartItemsByEmail("test@example.com"))
                .thenReturn(List.of());

        mockMvc.perform(get("/checkout")
                        .principal(authentication))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }


    @Test
    void placeOrder_shouldRedirectToCartWhenCartIsEmpty()
            throws Exception {

        when(authentication.getName())
                .thenReturn("test@example.com");

        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(cartService.getCartItemsByEmail("test@example.com"))
                .thenReturn(List.of());

        mockMvc.perform(post("/checkout/place-order")
                        .param("paymentMethod", "COD")
                        .principal(authentication))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }


    @Test
    void placeOrder_shouldReturnCheckoutWithErrorForInvalidPaymentMethod()
            throws Exception {

        CartItem cartItem =
                createCartItem(1, 10, 50000);

        User user = new User();
        user.setEmail("test@example.com");

        when(authentication.getName())
                .thenReturn("test@example.com");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(cartService.getCartItemsByEmail("test@example.com"))
                .thenReturn(List.of(cartItem));

        mockMvc.perform(post("/checkout/place-order")
                        .param("paymentMethod", "INVALID")
                        .principal(authentication))

                .andExpect(status().isOk())
                .andExpect(view().name("checkout"))
                .andExpect(model().attribute(
                        "errorMessage",
                        "Please select a valid payment method."
                ))
                .andExpect(model().attribute(
                        "total",
                        50000.0
                ));

        verify(orderRepository, never())
                .save(any(Order.class));
    }


    @Test
    void placeOrder_shouldReturnCheckoutWithErrorForInvalidQuantity()
            throws Exception {

        CartItem cartItem =
                createCartItem(0, 10, 50000);

        User user = new User();
        user.setEmail("test@example.com");

        when(authentication.getName())
                .thenReturn("test@example.com");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(cartService.getCartItemsByEmail("test@example.com"))
                .thenReturn(List.of(cartItem));

        mockMvc.perform(post("/checkout/place-order")
                        .param("paymentMethod", "COD")
                        .principal(authentication))

                .andExpect(status().isOk())
                .andExpect(view().name("checkout"))
                .andExpect(model().attribute(
                        "errorMessage",
                        "Product quantity must be greater than 0."
                ));

        verify(orderRepository, never())
                .save(any(Order.class));
    }


    @Test
    void placeOrder_shouldReturnCheckoutWithErrorWhenStockIsInsufficient()
            throws Exception {

        CartItem cartItem =
                createCartItem(11, 10, 50000);

        User user = new User();
        user.setEmail("test@example.com");

        when(authentication.getName())
                .thenReturn("test@example.com");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(cartService.getCartItemsByEmail("test@example.com"))
                .thenReturn(List.of(cartItem));

        mockMvc.perform(post("/checkout/place-order")
                        .param("paymentMethod", "COD")
                        .principal(authentication))

                .andExpect(status().isOk())
                .andExpect(view().name("checkout"))
                .andExpect(model().attribute(
                        "errorMessage",
                        "Not enough stock available for Laptop"
                ));

        verify(orderRepository, never())
                .save(any(Order.class));
    }


    @Test
    void placeOrder_shouldPlaceOrderSuccessfully()
            throws Exception {

        CartItem cartItem =
                createCartItem(2, 10, 50000);

        User user = new User();
        user.setEmail("test@example.com");

        Order savedOrder = new Order();

        when(authentication.getName())
                .thenReturn("test@example.com");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(cartService.getCartItemsByEmail("test@example.com"))
                .thenReturn(List.of(cartItem));

        when(orderRepository.save(any(Order.class)))
                .thenReturn(savedOrder);

        mockMvc.perform(post("/checkout/place-order")
                        .param("paymentMethod", "COD")
                        .principal(authentication))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"));

        verify(orderRepository)
                .save(any(Order.class));

        verify(orderItemRepository)
                .save(any());

        verify(cartService)
                .clearCartByEmail("test@example.com");
    }
}