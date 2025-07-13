package com.appgarage.pekkocrudapi.controller;

import com.appgarage.pekkocrudapi.actor.ProductMessages;
import com.appgarage.pekkocrudapi.model.Product;
import com.appgarage.pekkocrudapi.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;


/**
 * REST controller for product CRUD operations and retrieving all products.
 * Handles HTTP requests and delegates to ProductService.
 */
@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<?>> createProduct(@Valid @RequestBody Product product) {
        return productService.createProduct(product)
                .thenApply(response -> {
                    if (response.getError() != null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response.getError());
                    }
                    return ResponseEntity.status(HttpStatus.CREATED).body(response.getProduct());
                });
    }

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<?>> getProduct(@PathVariable String id) {
        return productService.getProduct(id)
                .thenApply(response -> {
                    if (response.getError() != null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response.getError());
                    }
                    return ResponseEntity.ok(response.getProduct());
                });
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<?>> getAllProducts() {
        return productService.getAllProducts()
                .thenApply(response -> {
                    if (response.getError() != null) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response.getError());
                    }
                    return ResponseEntity.ok(response.getProducts());
                });
    }

    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<?>> updateProduct(@PathVariable String id, @Valid @RequestBody Product product) {
        return productService.updateProduct(id, product)
                .thenApply(response -> {
                    if (response.getError() != null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response.getError());
                    }
                    return ResponseEntity.ok(response.getProduct());
                });
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<?>> deleteProduct(@PathVariable String id) {
        return productService.deleteProduct(id)
                .thenApply(response -> {
                    if (response.getError() != null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response.getError());
                    }
                    return ResponseEntity.noContent().build();
                });
    }
    // Some More Method
}