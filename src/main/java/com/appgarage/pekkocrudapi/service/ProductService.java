package com.appgarage.pekkocrudapi.service;

import com.appgarage.pekkocrudapi.actor.ProductActor;
import com.appgarage.pekkocrudapi.actor.ProductMessages;
import com.appgarage.pekkocrudapi.model.Product;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.routing.RoundRobinPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.apache.pekko.pattern.PatternsCS.ask;


/**
 * Service layer to interact with Pekko actors for product operations.
 * Uses RoundRobinPool for load balancing across multiple actor instances.
 */
@Service
public class ProductService {

    private final ActorRef productRouter;

    @Autowired
    public ProductService(ActorSystem actorSystem) {
        // Create a router with 5 actor instances for concurrency
        this.productRouter = actorSystem.actorOf(
                new RoundRobinPool(5).props(ProductActor.props()),
                "productRouter"
        );
    }

    public CompletableFuture<ProductMessages.ProductResponse> createProduct(Product product) {
        return ask(productRouter, new ProductMessages.CreateProduct(product), 5000)
                .toCompletableFuture()
                .thenApply(response -> (ProductMessages.ProductResponse) response);
    }

    public CompletableFuture<ProductMessages.ProductResponse> getProduct(String id) {
        return ask(productRouter, new ProductMessages.GetProduct(id), 5000)
                .toCompletableFuture()
                .thenApply(response -> (ProductMessages.ProductResponse) response);
    }

    public CompletableFuture<ProductMessages.ProductResponse> updateProduct(String id, Product product) {
        return ask(productRouter, new ProductMessages.UpdateProduct(id, product), 5000)
                .toCompletableFuture()
                .thenApply(response -> (ProductMessages.ProductResponse) response);
    }

    public CompletableFuture<ProductMessages.ProductResponse> deleteProduct(String id) {
        return ask(productRouter, new ProductMessages.DeleteProduct(id), 5000)
                .toCompletableFuture()
                .thenApply(response -> (ProductMessages.ProductResponse) response);
    }

    public CompletableFuture<ProductMessages.ProductListResponse> getAllProducts() {
        return ask(productRouter, new ProductMessages.GetAllProducts(), 5000)
                .toCompletableFuture()
                .thenApply(response -> (ProductMessages.ProductListResponse) response);
    }
}