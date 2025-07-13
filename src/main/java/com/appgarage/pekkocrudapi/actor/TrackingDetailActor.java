package com.appgarage.pekkocrudapi.actor;

import com.appgarage.pekkocrudapi.model.TrackingDetail;
import com.appgarage.pekkocrudapi.model.ErrorMessage;
import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.Props;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Actor responsible for handling TrackingDetail CRUD operations.
 * Uses ConcurrentHashMap for thread-safe storage (aligned with USPS code).
 */
public class TrackingDetailActor extends AbstractActor {

    private final Map<String, TrackingDetail> trackingStore = new ConcurrentHashMap<>();
    private final String apiVersion;

    public TrackingDetailActor(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public static Props props(String apiVersion) {
        return Props.create(TrackingDetailActor.class, apiVersion);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(TrackingDetailMessages.CreateTrackingDetail.class, this::handleCreate)
                .match(TrackingDetailMessages.GetTrackingDetail.class, this::handleGet)
                .match(TrackingDetailMessages.UpdateTrackingDetail.class, this::handleUpdate)
                .match(TrackingDetailMessages.DeleteTrackingDetail.class, this::handleDelete)
                .match(TrackingDetailMessages.GetAllTrackingDetails.class, this::handleGetAll)
                .build();
    }

    private void handleCreate(TrackingDetailMessages.CreateTrackingDetail msg) {
        TrackingDetail trackingDetail = msg.getTrackingDetail();
        if (trackingStore.containsKey(trackingDetail.getTrackingNumber())) {
            ErrorMessage error = createErrorMessage("150004", "Duplicate tracking number");
            sender().tell(new TrackingDetailMessages.TrackingDetailResponse(null, error), self());
        } else {
            trackingStore.put(trackingDetail.getTrackingNumber(), trackingDetail);
            sender().tell(new TrackingDetailMessages.TrackingDetailResponse(trackingDetail, null), self());
        }
    }

    private void handleGet(TrackingDetailMessages.GetTrackingDetail msg) {
        TrackingDetail trackingDetail = trackingStore.get(msg.getTrackingNumber());
        ErrorMessage error = trackingDetail == null ? createErrorMessage("150002", "Tracking detail not found") : null;
        sender().tell(new TrackingDetailMessages.TrackingDetailResponse(trackingDetail, error), self());
    }

    private void handleUpdate(TrackingDetailMessages.UpdateTrackingDetail msg) {
        if (trackingStore.containsKey(msg.getTrackingNumber())) {
            TrackingDetail trackingDetail = msg.getTrackingDetail();
            trackingDetail.setTrackingNumber(msg.getTrackingNumber());
            trackingStore.put(msg.getTrackingNumber(), trackingDetail);
            sender().tell(new TrackingDetailMessages.TrackingDetailResponse(trackingDetail, null), self());
        } else {
            ErrorMessage error = createErrorMessage("150002", "Tracking detail not found");
            sender().tell(new TrackingDetailMessages.TrackingDetailResponse(null, error), self());
        }
    }

    private void handleDelete(TrackingDetailMessages.DeleteTrackingDetail msg) {
        TrackingDetail trackingDetail = trackingStore.remove(msg.getTrackingNumber());
        ErrorMessage error = trackingDetail == null ? createErrorMessage("150002", "Tracking detail not found") : null;
        sender().tell(new TrackingDetailMessages.TrackingDetailResponse(trackingDetail, error), self());
    }

    private void handleGetAll(TrackingDetailMessages.GetAllTrackingDetails msg) {
        List<TrackingDetail> trackingDetails = new ArrayList<>(trackingStore.values());
        sender().tell(new TrackingDetailMessages.TrackingDetailListResponse(trackingDetails, null), self());
    }

    private ErrorMessage createErrorMessage(String returnCode, String message) {
        ErrorMessage error = new ErrorMessage();
        error.setApiVersion(apiVersion);
        error.setReturnCode(returnCode);
        error.setTrackingNumber(null);
        return error;
    }
}
