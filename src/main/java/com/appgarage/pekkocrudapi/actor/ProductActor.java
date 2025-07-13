package com.appgarage.pekkocrudapi.actor;

import com.appgarage.pekkocrudapi.model.Product;
import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.Props;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


import java.util.ArrayList;


/**
 * Actor responsible for handling product CRUD operations and retrieving all products.
 * Maintains an in-memory store for simplicity (replace with DB in production).
 */
public class ProductActor extends AbstractActor {
    private final static Map<String, Product> productStore = new HashMap<>();

    public static Props props() {
        return Props.create(ProductActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ProductMessages.CreateProduct.class, this::handleCreate)
                .match(ProductMessages.GetProduct.class, this::handleGet)
                .match(ProductMessages.UpdateProduct.class, this::handleUpdate)
                .match(ProductMessages.DeleteProduct.class, this::handleDelete)
                .match(ProductMessages.GetAllProducts.class, this::handleGetAll)
                .build();
    }

    private void handleCreate(ProductMessages.CreateProduct msg) {
        Product product = msg.getProduct();
        String id = UUID.randomUUID().toString();
        product.setId(id);
        productStore.put(id, product);
        sender().tell(new ProductMessages.ProductResponse(product, null), self());
    }

    private void handleGet(ProductMessages.GetProduct msg) {
        Product product = productStore.get(msg.getId());
        String error = product == null ? "Product not found" : null;
        sender().tell(new ProductMessages.ProductResponse(product, error), self());
    }

    private void handleUpdate(ProductMessages.UpdateProduct msg) {
        if (productStore.containsKey(msg.getId())) {
            Product product = msg.getProduct();
            product.setId(msg.getId());
            productStore.put(msg.getId(), product);
            sender().tell(new ProductMessages.ProductResponse(product, null), self());
        } else {
            sender().tell(new ProductMessages.ProductResponse(null, "Product not found"), self());
        }
    }

    private void handleDelete(ProductMessages.DeleteProduct msg) {
        Product product = productStore.remove(msg.getId());
        String error = product == null ? "Product not found" : null;
        sender().tell(new ProductMessages.ProductResponse(product, error), self());
    }

    private void handleGetAll(ProductMessages.GetAllProducts msg) {
        sender().tell(new ProductMessages.ProductListResponse(new ArrayList<>(productStore.values()), null), self());
    }
}