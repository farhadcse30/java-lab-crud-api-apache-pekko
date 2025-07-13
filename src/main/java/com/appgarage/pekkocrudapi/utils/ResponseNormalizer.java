package com.appgarage.pekkocrudapi.utils;

import com.appgarage.pekkocrudapi.model.TrackingDetail;

import java.util.Optional;

/**
 * Utility class to normalize TrackingDetail responses.
 * Sets unused fields to null, inspired by TrackingApiResponseNormalizer.
 */
public class ResponseNormalizer {

    private ResponseNormalizer() {
        // Private constructor to prevent instantiation
    }

    /**
     * Normalizes a TrackingDetail by setting unused fields to null.
     * @param trackingDetail the TrackingDetail to normalize
     */
    public static void normalizeTrackingDetail(TrackingDetail trackingDetail) {
        if (trackingDetail == null) {
            return;
        }
        // Set unused fields to null (example fields for future extension)
        Optional.ofNullable(trackingDetail.getDestinationZipCode())
                .filter(zip -> !zip.matches("\\d{5}(-\\d{4})?"))
                .ifPresent(zip -> trackingDetail.setDestinationZipCode(null));
    }
}
