package com.appgarage.pekkocrudapi.service;

import com.appgarage.pekkocrudapi.actor.TrackingDetailMessages;
import com.appgarage.pekkocrudapi.model.TrackingDetail;
import org.apache.pekko.actor.ActorRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import static org.apache.pekko.pattern.PatternsCS.ask;

/**
 * Service layer to interact with Pekko actors for TrackingDetail operations.
 * Uses RoundRobinPool for load balancing, similar to ProductService and UserService.
 */
@Service
public class TrackingDetailService {

    private final ActorRef trackingDetailRouter;
    private final String apiVersion;

    @Autowired
    public TrackingDetailService(ActorRef trackingDetailRouter, @Value("${api.version}") String apiVersion) {
        this.trackingDetailRouter = trackingDetailRouter;
        this.apiVersion = apiVersion;
    }

    public CompletableFuture<TrackingDetailMessages.TrackingDetailResponse> createTrackingDetail(TrackingDetail trackingDetail) {
        return ask(trackingDetailRouter, new TrackingDetailMessages.CreateTrackingDetail(trackingDetail), 5000)
                .toCompletableFuture()
                .thenApply(response -> (TrackingDetailMessages.TrackingDetailResponse) response);
    }

    public CompletableFuture<TrackingDetailMessages.TrackingDetailResponse> getTrackingDetail(String trackingNumber) {
        return ask(trackingDetailRouter, new TrackingDetailMessages.GetTrackingDetail(trackingNumber), 5000)
                .toCompletableFuture()
                .thenApply(response -> (TrackingDetailMessages.TrackingDetailResponse) response);
    }

    public CompletableFuture<TrackingDetailMessages.TrackingDetailResponse> updateTrackingDetail(String trackingNumber, TrackingDetail trackingDetail) {
        return ask(trackingDetailRouter, new TrackingDetailMessages.UpdateTrackingDetail(trackingNumber, trackingDetail), 5000)
                .toCompletableFuture()
                .thenApply(response -> (TrackingDetailMessages.TrackingDetailResponse) response);
    }

    public CompletableFuture<TrackingDetailMessages.TrackingDetailResponse> deleteTrackingDetail(String trackingNumber) {
        return ask(trackingDetailRouter, new TrackingDetailMessages.DeleteTrackingDetail(trackingNumber), 5000)
                .toCompletableFuture()
                .thenApply(response -> (TrackingDetailMessages.TrackingDetailResponse) response);
    }

    public CompletableFuture<TrackingDetailMessages.TrackingDetailListResponse> getAllTrackingDetails() {
        return ask(trackingDetailRouter, new TrackingDetailMessages.GetAllTrackingDetails(), 5000)
                .toCompletableFuture()
                .thenApply(response -> (TrackingDetailMessages.TrackingDetailListResponse) response);
    }
}