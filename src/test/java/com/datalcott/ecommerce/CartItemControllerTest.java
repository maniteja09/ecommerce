package com.datalcott.ecommerce;

import com.datalcott.ecommerce.controller.CartItemController;
import com.datalcott.ecommerce.entity.CartItem;
import com.datalcott.ecommerce.service.CartItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CartItemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CartItemService cartItemService;

    @InjectMocks
    private CartItemController cartItemController;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(cartItemController)
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

    @Test
    void getAllCartItems_shouldReturnCartItemsPage()
            throws Exception {

        CartItem cartItem = new CartItem();

        when(cartItemService.getAllCartItems())
                .thenReturn(List.of(cartItem));

        mockMvc.perform(get("/cart-items"))

                .andExpect(status().isOk())
                .andExpect(view().name("cart-items"))
                .andExpect(model().attributeExists("cartItems"));

        verify(cartItemService)
                .getAllCartItems();
    }

    @Test
    void saveCartItem_shouldRedirectToCart()
            throws Exception {

        CartItem cartItem = new CartItem();

        when(cartItemService.saveCartItem(any(CartItem.class)))
                .thenReturn(cartItem);

        mockMvc.perform(
                        post("/cart-items/save")
                                .param("quantity", "2")
                )

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(cartItemService)
                .saveCartItem(any(CartItem.class));
    }
}