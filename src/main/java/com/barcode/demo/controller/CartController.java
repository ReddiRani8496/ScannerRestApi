package com.barcode.demo.controller;

import com.barcode.demo.model.Cart;
import com.barcode.demo.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/{userId}/add/{code}")
    public Cart addToCart(@PathVariable String userId, @PathVariable String code) {
        return cartService.addToCart(userId, code);
    }

    @PutMapping("/{userId}/update")
    public Cart updateQuantity(@PathVariable String userId,
                               @RequestParam String code,
                               @RequestParam int quantity) {
        return cartService.updateQuantity(userId, code, quantity);
    }
}
