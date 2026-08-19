package com.datalcott.ecommerce;

import com.datalcott.ecommerce.controller.CartController;
import com.datalcott.ecommerce.entity.Cart;
import com.datalcott.ecommerce.entity.CartItem;
import com.datalcott.ecommerce.entity.Product;
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
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CartController cartController;

    private MockMvc mockMvc;

    private Cart cart;
    private CartItem cartItem;
    private Product product;


    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(cartController)
                .setViewResolvers((viewName, locale) -> {

                    if (viewName.startsWith("redirect:")) {
                        return new RedirectView(viewName.substring(9));
                    }

                    return (View) (model, request, response) -> {
                    };
                })
                .build();


        cart = new Cart();
        cart.setId(1L);


        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(50000);
        product.setStock(10);


        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
    }


    // =========================================================
    // 1. GET /cart
    // =========================================================

    @Test
    void viewCart_shouldReturnCartPage() throws Exception {

        when(authentication.getName())
                .thenReturn("test@example.com");

        when(cartService.getOrCreateCart("test@example.com"))
                .thenReturn(cart);

        when(cartService.getCartItems(1L))
                .thenReturn(List.of(cartItem));


        mockMvc.perform(
                        get("/cart")
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("cartItems"))
                .andExpect(model().attribute("total", 100000.0));


        verify(cartService)
                .getOrCreateCart("test@example.com");

        verify(cartService)
                .getCartItems(1L);
    }


    // =========================================================
    // 2. GET /cart/new
    // =========================================================

    @Test
    void showCartForm_shouldReturnCartForm() throws Exception {

        mockMvc.perform(
                        get("/cart/new")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("cart-form"))
                .andExpect(model().attributeExists("cart"));
    }


    // =========================================================
    // 3. POST /cart/save
    // =========================================================

    @Test
    void saveCart_shouldRedirectToCart() throws Exception {

        when(cartService.saveCart(any(Cart.class)))
                .thenReturn(cart);


        mockMvc.perform(
                        post("/cart/save")
                                .param("id", "1")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));


        verify(cartService)
                .saveCart(any(Cart.class));
    }


    // =========================================================
    // 4. POST /cart/add/{productId}
    // =========================================================

    @Test
    void addToCart_shouldRedirectToCart() throws Exception {

        when(authentication.getName())
                .thenReturn("test@example.com");


        mockMvc.perform(
                        post("/cart/add/1")
                                .param("quantity", "2")
                                .principal(authentication)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));


        verify(cartService)
                .addToCart(
                        "test@example.com",
                        1L,
                        2
                );
    }


    // =========================================================
    // 5. POST /cart/remove/{cartItemId}
    // =========================================================

    @Test
    void removeFromCart_shouldRedirectToCart() throws Exception {

        when(authentication.getName())
                .thenReturn("test@example.com");


        mockMvc.perform(
                        post("/cart/remove/1")
                                .principal(authentication)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));


        verify(cartService)
                .removeFromCart(
                        "test@example.com",
                        1L
                );
    }


    // =========================================================
    // 6. POST /cart/update/{cartItemId}
    // =========================================================

    @Test
    void updateQuantity_shouldRedirectToCart() throws Exception {

        when(authentication.getName())
                .thenReturn("test@example.com");


        mockMvc.perform(
                        post("/cart/update/1")
                                .param("quantity", "5")
                                .principal(authentication)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));


        verify(cartService)
                .updateQuantity(
                        "test@example.com",
                        1L,
                        5
                );
    }
}