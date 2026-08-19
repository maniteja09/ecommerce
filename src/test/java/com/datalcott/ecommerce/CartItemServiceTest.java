package com.datalcott.ecommerce;

import com.datalcott.ecommerce.entity.CartItem;
import com.datalcott.ecommerce.repository.CartItemRepository;
import com.datalcott.ecommerce.service.CartItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartItemService cartItemService;


    @Test
    void saveCartItem_shouldReturnSavedCartItem() {

        CartItem cartItem = new CartItem();

        when(cartItemRepository.save(cartItem))
                .thenReturn(cartItem);

        CartItem result =
                cartItemService.saveCartItem(cartItem);

        assertEquals(cartItem, result);

        verify(cartItemRepository)
                .save(cartItem);
    }


    @Test
    void getAllCartItems_shouldReturnAllCartItems() {

        CartItem cartItem = new CartItem();

        when(cartItemRepository.findAll())
                .thenReturn(List.of(cartItem));

        List<CartItem> result =
                cartItemService.getAllCartItems();

        assertEquals(1, result.size());
        assertEquals(cartItem, result.get(0));

        verify(cartItemRepository)
                .findAll();
    }


    @Test
    void getCartItemById_shouldReturnCartItem() {

        CartItem cartItem = new CartItem();

        when(cartItemRepository.findById(1L))
                .thenReturn(Optional.of(cartItem));

        CartItem result =
                cartItemService.getCartItemById(1L);

        assertEquals(cartItem, result);

        verify(cartItemRepository)
                .findById(1L);
    }


    @Test
    void deleteCartItem_shouldDeleteCartItem() {

        doNothing()
                .when(cartItemRepository)
                .deleteById(1L);

        cartItemService.deleteCartItem(1L);

        verify(cartItemRepository)
                .deleteById(1L);
    }
}