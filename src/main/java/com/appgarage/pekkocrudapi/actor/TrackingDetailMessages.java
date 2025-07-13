package com.appgarage.pekkocrudapi.actor;

import com.appgarage.pekkocrudapi.model.TrackingDetail;
import com.appgarage.pekkocrudapi.model.ErrorMessage;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Message classes for Pekko actor communication for TrackingDetail operations.
 * Includes request and response messages for CRUD operations.
 */
public class TrackingDetailMessages {

    /**
     * Request to create a new TrackingDetail.
     */
    public static class CreateTrackingDetail implements Serializable {
        private final TrackingDetail trackingDetail;

        public CreateTrackingDetail(TrackingDetail trackingDetail) {
            this.trackingDetail = trackingDetail;
        }

        public TrackingDetail getTrackingDetail() {
            return trackingDetail;
        }
    }

    /**
     * Request to retrieve a TrackingDetail by tracking number.
     */
    public static class GetTrackingDetail implements Serializable {
        private final String trackingNumber;

        public GetTrackingDetail(String trackingNumber) {
            this.trackingNumber = trackingNumber;
        }

        public String getTrackingNumber() {
            return trackingNumber;
        }
    }

    /**
     * Request to update an existing TrackingDetail.
     */
    public static class UpdateTrackingDetail implements Serializable {
        private final String trackingNumber;
        private final TrackingDetail trackingDetail;

        public UpdateTrackingDetail(String trackingNumber, TrackingDetail trackingDetail) {
            this.trackingNumber = trackingNumber;
            this.trackingDetail = trackingDetail;
        }

        public String getTrackingNumber() {
            return trackingNumber;
        }

        public TrackingDetail getTrackingDetail() {
            return trackingDetail;
        }
    }

    /**
     * Request to delete a TrackingDetail by tracking number.
     */
    public static class DeleteTrackingDetail implements Serializable {
        private final String trackingNumber;

        public DeleteTrackingDetail(String trackingNumber) {
            this.trackingNumber = trackingNumber;
        }

        public String getTrackingNumber() {
            return trackingNumber;
        }
    }

    /**
     * Request to retrieve all TrackingDetails.
     */
    public static class GetAllTrackingDetails implements Serializable {
        // No fields needed
    }

    /**
     * Response for single TrackingDetail operations (Create, Get, Update, Delete).
     */
    public static class TrackingDetailResponse implements Serializable {
        private final TrackingDetail trackingDetail;
        private final ErrorMessage error;

        public TrackingDetailResponse(TrackingDetail trackingDetail, ErrorMessage error) {
            this.trackingDetail = trackingDetail;
            this.error = error;
        }

        public TrackingDetail getTrackingDetail() {
            return trackingDetail;
        }

        public ErrorMessage getError() {
            return error;
        }
    }

    /**
     * Response for GetAllTrackingDetails operation.
     */
    public static class TrackingDetailListResponse implements Serializable {
        private final List<TrackingDetail> trackingDetails;
        private final ErrorMessage error;

        public TrackingDetailListResponse(List<TrackingDetail> trackingDetails, ErrorMessage error) {
            this.trackingDetails = trackingDetails;
            this.error = error;
        }

        public List<TrackingDetail> getTrackingDetails() {
            return trackingDetails;
        }

        public ErrorMessage getError() {
            return error;
        }
    }
}
