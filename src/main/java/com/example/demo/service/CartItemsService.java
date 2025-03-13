package com.example.demo.service;

import com.example.demo.model.CartItems;
import com.example.demo.repository.CartItemsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartItemsService {
    private final CartItemsRepository cartItemsRepository;

    public CartItemsService(CartItemsRepository cartItemsRepository) {
        this.cartItemsRepository = cartItemsRepository;
    }

    public CartItems addCartItem(CartItems cartItems) {
//        cartItems.setI
        return cartItemsRepository.save(cartItems);
    }

    public Page<CartItems> getAllCartItems(Pageable pageable) {
        Page<CartItems> cartItems = cartItemsRepository.findAll(pageable);
        return cartItems;
    }

    void deleteCartItemByProductId(Long productId) {
        cartItemsRepository.deleteByProductId(productId);
    }
    public CartItems getCartItemById(Long id) {
        return cartItemsRepository.findById(id).orElse(null);
    }
    public CartItems updateCartItem(CartItems cartItems) {
        return cartItemsRepository.save(cartItems);
    }
    public void deleteCartItemById(Long id) {
        cartItemsRepository.deleteById(id);
    }
}
