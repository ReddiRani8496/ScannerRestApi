package com.barcode.demo.service;

import com.barcode.demo.model.Cart;
import com.barcode.demo.model.CartItem;
import com.barcode.demo.model.Product;
import com.barcode.demo.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CartService {
    @Autowired
    private final CartRepository cartRepository;
    @Autowired
    private final ProductService productService;

    public CartService(CartRepository cartRepository, ProductService productService) {
        this.cartRepository = cartRepository;
        this.productService = productService;
    }

    public Cart addToCart(String userId, String code) {
        Product product = productService.findByCode(code);
        Cart cart = cartRepository.findByUserId(userId)
                .orElse(new Cart(null, userId, new ArrayList<>(), 0.0));

        CartItem newItem = new CartItem(product.getCode(), product.getName(),
                product.getMrp(), product.getDiscount(), 1);

        // Add or update item
        updateCart(cart, newItem);
        return cartRepository.save(cart);
    }

    public Cart updateQuantity(String userId, String code, int quantity) {
        System.out.println("inside update quantity");
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.getItems().stream()
                .filter(item -> item.getCode().equals(code))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));
        System.out.println("inside update total " + cart);
        updateTotal(cart);
        return cartRepository.save(cart);
    }

    private void updateTotal(Cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(item -> (item.getMrp() - item.getDiscount()) * item.getQuantity())
                .sum();
                System.out.println("total "+ total);
        cart.setTotal(total);
    }

    private void updateCart(Cart cart, CartItem newItem) {
        // Check if the item already exists in the cart
        System.out.println("cart " + cart);
        System.out.println("new item "+ newItem);
        boolean found = false;
        for (CartItem item : cart.getItems()) {
            if (item.getCode().equals(newItem.getCode())) {
                // If found, increase the quantity
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
                found = true;
                break;
            }
        }
        // If not found, add as a new item
        if (!found) {
            cart.getItems().add(newItem);
        }
        // Update the total after modifying the cart
        updateTotal(cart);
    }
    
}
