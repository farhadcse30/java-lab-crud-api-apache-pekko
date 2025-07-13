package com.appgarage.pekkocrudapi.actor;

import com.appgarage.pekkocrudapi.model.Product;
import java.io.Serializable;

import java.util.List;

/**
 * Message classes for Pekko actor communication.
 */
public class ProductMessages {

    public static class CreateProduct implements Serializable {
        private final Product product;

        public CreateProduct(Product product) {
            this.product = product;
        }

        public Product getProduct() {
            return product;
        }
    }

    public static class GetProduct implements Serializable {
        private final String id;

        public GetProduct(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    public static class UpdateProduct implements Serializable {
        private final String id;
        private final Product product;

        public UpdateProduct(String id, Product product) {
            this.id = id;
            this.product = product;
        }

        public String getId() {
            return id;
        }

        public Product getProduct() {
            return product;
        }
    }

    public static class DeleteProduct implements Serializable {
        private final String id;

        public DeleteProduct(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    public static class GetAllProducts implements Serializable {
        // No fields needed for this message
    }

    public static class ProductResponse implements Serializable {
        private final Product product;
        private final String error;

        public ProductResponse(Product product, String error) {
            this.product = product;
            this.error = error;
        }

        public Product getProduct() {
            return product;
        }

        public String getError() {
            return error;
        }
    }

    public static class ProductListResponse implements Serializable {
        private final List<Product> products;
        private final String error;

        public ProductListResponse(List<Product> products, String error) {
            this.products = products;
            this.error = error;
        }

        public List<Product> getProducts() {
            return products;
        }

        public String getError() {
            return error;
        }
    }
}