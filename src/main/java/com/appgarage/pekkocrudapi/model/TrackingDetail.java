package com.appgarage.pekkocrudapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * TrackingDetail model representing a tracking entity with validation constraints.
 * Implements Serializable for Pekko message passing.
 * Fields inspired by TrackingRequestInfo from USPS tracking API.
 */
public class TrackingDetail implements Serializable {

    @NotBlank(message = "Tracking number is mandatory")
    @JsonProperty("trackingNumber")
    private String trackingNumber;

    @JsonProperty("mailingDate")
    private LocalDate mailingDate;

    @JsonProperty("destinationZipCode")
    private String destinationZipCode;

    // No-args constructor for Jackson (optional)
    public TrackingDetail() {
    }

    // Constructor for JSON deserialization
    public TrackingDetail(@JsonProperty("trackingNumber") String trackingNumber,
                          @JsonProperty("mailingDate") LocalDate mailingDate,
                          @JsonProperty("destinationZipCode") String destinationZipCode) {
        this.trackingNumber = trackingNumber;
        this.mailingDate = mailingDate;
        this.destinationZipCode = destinationZipCode;
    }

    // Getters and Setters
    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public LocalDate getMailingDate() {
        return mailingDate;
    }

    public void setMailingDate(LocalDate mailingDate) {
        this.mailingDate = mailingDate;
    }

    public String getDestinationZipCode() {
        return destinationZipCode;
    }

    public void setDestinationZipCode(String destinationZipCode) {
        this.destinationZipCode = destinationZipCode;
    }
}