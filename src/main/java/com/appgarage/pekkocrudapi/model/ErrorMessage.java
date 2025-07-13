package com.appgarage.pekkocrudapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Error message model for API responses, inspired by USPS tracking API.
 * Contains API version, return code, and a descriptive message.
 */
public class ErrorMessage {

    @JsonProperty("apiVersion")
    private String apiVersion;

    @JsonProperty("returnCode")
    private String returnCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("trackingNumber")
    private String trackingNumber;

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
}