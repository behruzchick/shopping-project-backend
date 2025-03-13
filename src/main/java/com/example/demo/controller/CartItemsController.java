package com.example.demo.controller;

import com.example.demo.model.CartItems;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repository.ProductRepository;
import com.example.demo.security.SecurityUtils;
import com.example.demo.security.TokenProvider;
import com.example.demo.service.CartItemsService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CartItemsController {
    private final CartItemsService cartItemsService;
    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final ProductRepository productRepository;

    public CartItemsController(CartItemsService cartItemsService, UserService userService, TokenProvider tokenProvider, ProductRepository productRepository) {
        this.cartItemsService = cartItemsService;
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.productRepository = productRepository;
    }


    @PostMapping("/cart-items/create")
    public ResponseEntity<?> createCartItem(@RequestBody CartItems cartItems) {
        CartItems newCartItem = cartItemsService.addCartItem(cartItems);
        return ResponseEntity.ok(newCartItem);
    }

    @GetMapping("/cart-items/all")
    public ResponseEntity<?> getAllCartItems(Pageable pageable) {

        Page<CartItems> cartItems = cartItemsService.getAllCartItems(pageable);

        if (cartItems.getTotalElements() > 0) {
            return ResponseEntity.ok(cartItems.getContent());
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No cart items found");
        }

    }

    @GetMapping("/cart-items/{id}")
    public ResponseEntity<?> getCartItem(@PathVariable Long id) {
        CartItems cartItem =  cartItemsService.getCartItemById(id);
        if (cartItem == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No cart item found");
        }
        return ResponseEntity.ok(cartItem);
    }

    @DeleteMapping("/cart-items/{id}")
    public ResponseEntity<?> deleteCartItem(@PathVariable Long id) {
        CartItems cartItem =  cartItemsService.getCartItemById(id);
        if (cartItem == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No cart item found");
        }else {
            cartItemsService.deleteCartItemById(id);
            return ResponseEntity.ok().build();
        }
    }
    @PutMapping("/cart-items/{id}")
    public ResponseEntity<?> updateCartItem(@PathVariable Long id, @RequestBody CartItems cartItems) {
        if (cartItems == null) {    
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No cart item found");
        }
        CartItems currentCartItem = cartItemsService.getCartItemById(id);
        if (currentCartItem == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No cart item found");
        }

        currentCartItem.setQuantity(cartItems.getQuantity());
        cartItemsService.updateCartItem(currentCartItem);
        return ResponseEntity.ok().build();
    }
}
