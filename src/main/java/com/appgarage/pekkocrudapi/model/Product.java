package com.appgarage.pekkocrudapi.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.io.Serializable;

/**
 * Product model representing the entity with validation constraints.
 * Implements Serializable for Pekko message passing.
 */
public class Product implements Serializable {
    private String id;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @Positive(message = "Price must be positive")
    private double price;

    @NotBlank(message = "Description is mandatory")
    private String description;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}