package com.appgarage.pekkocrudapi.routes;

import com.appgarage.pekkocrudapi.actor.TrackingDetailMessages;
import com.appgarage.pekkocrudapi.model.ErrorMessage;
import com.appgarage.pekkocrudapi.model.TrackingDetail;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.http.javadsl.marshalling.Marshaller;
import org.apache.pekko.http.javadsl.model.StatusCodes;
import org.apache.pekko.http.javadsl.server.AllDirectives;
import org.apache.pekko.http.javadsl.server.PathMatchers;
import org.apache.pekko.http.javadsl.server.Route;
import org.apache.pekko.http.javadsl.server.RejectionHandler;
import org.apache.pekko.http.javadsl.server.ExceptionHandler;
import org.apache.pekko.http.javadsl.unmarshalling.Unmarshaller;
import org.apache.pekko.http.javadsl.marshallers.jackson.Jackson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import scala.jdk.javaapi.FutureConverters;

import java.util.List;
import java.util.concurrent.CompletionStage;

import static org.apache.pekko.http.javadsl.server.Directives.*;
import static org.apache.pekko.pattern.Patterns.ask;

/**
 * Pekko HTTP route for TrackingDetail CRUD operations.
 * Inspired by TrackingDetailApiRoute and AbstractRouteWithJsonBody from USPS code.
 * Handles JSON requests and responses with custom error handling.
 * Converts Scala Futures to Java CompletionStage for compatibility with Pekko HTTP directives.
 * Uses Jackson marshaller for serializing response objects to JSON.
 * Includes custom RejectionHandler and ExceptionHandler for robust error handling.
 */
@Component
public class TrackingDetailApiRoute extends AllDirectives {

    private static final Logger log = LoggerFactory.getLogger(TrackingDetailApiRoute.class);
    private final ActorSystem actorSystem;
    private final String apiVersion;
    private final ActorRef trackingDetailRouter;
    private final ObjectMapper objectMapper;

    @Autowired
    public TrackingDetailApiRoute(ActorSystem actorSystem, @Value("${api.version}") String apiVersion, ActorRef trackingDetailRouter, ObjectMapper objectMapper) {
        this.actorSystem = actorSystem;
        this.apiVersion = apiVersion;
        this.trackingDetailRouter = trackingDetailRouter;
        this.objectMapper = objectMapper;
    }

    public Route createRoute() {
        // Create a Jackson marshaller for serializing responses to JSON
        Marshaller<Object, org.apache.pekko.http.javadsl.model.RequestEntity> marshaller = Jackson.marshaller(objectMapper);

        // Custom RejectionHandler for handling invalid requests
        RejectionHandler rejectionHandler = RejectionHandler.newBuilder()
                .handleAll(org.apache.pekko.http.javadsl.server.Rejection.class, rejection -> {
                    log.warn("Request rejected: {}", rejection);
                    ErrorMessage error = new ErrorMessage();
                    error.setApiVersion(apiVersion);
                    error.setReturnCode("150003");
                    error.setMessage("Invalid request format or method not allowed");
                    return complete(StatusCodes.BAD_REQUEST, error, marshaller);
                })
                .build();

        // Custom ExceptionHandler for unexpected errors
        ExceptionHandler exceptionHandler = ExceptionHandler.newBuilder()
                .match(Exception.class, e -> {
                    log.error("Unexpected error processing request", e);
                    ErrorMessage error = new ErrorMessage();
                    error.setApiVersion(apiVersion);
                    error.setReturnCode("150000");
                    error.setMessage("Internal server error");
                    return complete(StatusCodes.INTERNAL_SERVER_ERROR, error, marshaller);
                })
                .build();

        return handleRejections(rejectionHandler, () ->
                handleExceptions(exceptionHandler, () ->
                        pathPrefix("api", () ->
                                pathPrefix("tracking-details", () ->
                                        route(
                                                // POST /api/tracking-details
                                                post(() ->
                                                        entity(Unmarshaller.entityToString(), content -> {
                                                            try {
                                                                log.info("Processing POST request with content: {}", content);
                                                                TrackingDetail trackingDetail = objectMapper.readValue(content, TrackingDetail.class);
                                                                // Convert Scala Future to Java CompletionStage
                                                                CompletionStage<Object> future = FutureConverters.asJava(ask(trackingDetailRouter, new TrackingDetailMessages.CreateTrackingDetail(trackingDetail), 5000));
                                                                return onSuccess(future, response -> {
                                                                    if (!(response instanceof TrackingDetailMessages.TrackingDetailResponse)) {
                                                                        log.error("Unexpected response type: {}", response.getClass().getName());
                                                                        ErrorMessage error = new ErrorMessage();
                                                                        error.setApiVersion(apiVersion);
                                                                        error.setReturnCode("150000");
                                                                        error.setMessage("Unexpected response from actor");
                                                                        return complete(StatusCodes.INTERNAL_SERVER_ERROR, error, marshaller);
                                                                    }
                                                                    TrackingDetailMessages.TrackingDetailResponse resp = (TrackingDetailMessages.TrackingDetailResponse) response;
                                                                    if (resp.getError() != null) {
                                                                        log.warn("Create failed: {}", resp.getError().getMessage());
                                                                        return complete(StatusCodes.BAD_REQUEST, resp.getError(), marshaller);
                                                                    }
                                                                    log.info("Created tracking detail: {}", resp.getTrackingDetail().getTrackingNumber());
                                                                    return complete(StatusCodes.CREATED, resp.getTrackingDetail(), marshaller);
                                                                });
                                                            } catch (Exception e) {
                                                                log.error("Invalid JSON in POST request", e);
                                                                ErrorMessage error = new ErrorMessage();
                                                                error.setApiVersion(apiVersion);
                                                                error.setReturnCode("150001");
                                                                error.setMessage("Invalid JSON format: " + e.getMessage());
                                                                return complete(StatusCodes.BAD_REQUEST, error, marshaller);
                                                            }
                                                        })
                                                ),

                                                // GET /api/tracking-details/{trackingNumber}
                                                path(PathMatchers.segment(), trackingNumber ->
                                                        get(() -> {
                                                            log.info("Processing GET request for tracking number: {}", trackingNumber);
                                                            // Convert Scala Future to Java CompletionStage
                                                            CompletionStage<Object> future = FutureConverters.asJava(ask(trackingDetailRouter, new TrackingDetailMessages.GetTrackingDetail(trackingNumber), 5000));
                                                            return onSuccess(future, response -> {
                                                                if (!(response instanceof TrackingDetailMessages.TrackingDetailResponse)) {
                                                                    log.error("Unexpected response type: {}", response.getClass().getName());
                                                                    ErrorMessage error = new ErrorMessage();
                                                                    error.setApiVersion(apiVersion);
                                                                    error.setReturnCode("150000");
                                                                    error.setMessage("Unexpected response from actor");
                                                                    return complete(StatusCodes.INTERNAL_SERVER_ERROR, error, marshaller);
                                                                }
                                                                TrackingDetailMessages.TrackingDetailResponse resp = (TrackingDetailMessages.TrackingDetailResponse) response;
                                                                if (resp.getError() != null) {
                                                                    log.warn("Tracking detail not found: {}", trackingNumber);
                                                                    return complete(StatusCodes.NOT_FOUND, resp.getError(), marshaller);
                                                                }
                                                                log.info("Retrieved tracking detail: {}", trackingNumber);
                                                                return complete(StatusCodes.OK, resp.getTrackingDetail(), marshaller);
                                                            });
                                                        })
                                                ),

                                                // GET /api/tracking-details
                                                get(() -> {
                                                    log.info("Processing GET request for all tracking details");
                                                    // Convert Scala Future to Java CompletionStage
                                                    CompletionStage<Object> future = FutureConverters.asJava(ask(trackingDetailRouter, new TrackingDetailMessages.GetAllTrackingDetails(), 5000));
                                                    return onSuccess(future, response -> {
                                                        if (!(response instanceof TrackingDetailMessages.TrackingDetailListResponse)) {
                                                            log.error("Unexpected response type: {}", response.getClass().getName());
                                                            ErrorMessage error = new ErrorMessage();
                                                            error.setApiVersion(apiVersion);
                                                            error.setReturnCode("150000");
                                                            error.setMessage("Unexpected response from actor");
                                                            return complete(StatusCodes.INTERNAL_SERVER_ERROR, error, marshaller);
                                                        }
                                                        TrackingDetailMessages.TrackingDetailListResponse resp = (TrackingDetailMessages.TrackingDetailListResponse) response;
                                                        if (resp.getError() != null) {
                                                            log.error("Failed to retrieve tracking details: {}", resp.getError().getMessage());
                                                            return complete(StatusCodes.INTERNAL_SERVER_ERROR, resp.getError(), marshaller);
                                                        }
                                                        log.info("Retrieved {} tracking details", resp.getTrackingDetails().size());
                                                        return complete(StatusCodes.OK, resp.getTrackingDetails(), marshaller);
                                                    });
                                                }),

                                                // PUT /api/tracking-details/{trackingNumber}
                                                path(PathMatchers.segment(), trackingNumber ->
                                                        put(() ->
                                                                entity(Unmarshaller.entityToString(), content -> {
                                                                    try {
                                                                        log.info("Processing PUT request for tracking number: {} with content: {}", trackingNumber, content);
                                                                        TrackingDetail trackingDetail = objectMapper.readValue(content, TrackingDetail.class);
                                                                        // Convert Scala Future to Java CompletionStage
                                                                        CompletionStage<Object> future = FutureConverters.asJava(ask(trackingDetailRouter, new TrackingDetailMessages.UpdateTrackingDetail(trackingNumber, trackingDetail), 5000));
                                                                        return onSuccess(future, response -> {
                                                                            if (!(response instanceof TrackingDetailMessages.TrackingDetailResponse)) {
                                                                                log.error("Unexpected response type: {}", response.getClass().getName());
                                                                                ErrorMessage error = new ErrorMessage();
                                                                                error.setApiVersion(apiVersion);
                                                                                error.setReturnCode("150000");
                                                                                error.setMessage("Unexpected response from actor");
                                                                                return complete(StatusCodes.INTERNAL_SERVER_ERROR, error, marshaller);
                                                                            }
                                                                            TrackingDetailMessages.TrackingDetailResponse resp = (TrackingDetailMessages.TrackingDetailResponse) response;
                                                                            if (resp.getError() != null) {
                                                                                log.warn("Update failed for tracking number: {}", trackingNumber);
                                                                                return complete(StatusCodes.NOT_FOUND, resp.getError(), marshaller);
                                                                            }
                                                                            log.info("Updated tracking detail: {}", trackingNumber);
                                                                            return complete(StatusCodes.OK, resp.getTrackingDetail(), marshaller);
                                                                        });
                                                                    } catch (Exception e) {
                                                                        log.error("Invalid JSON in PUT request", e);
                                                                        ErrorMessage error = new ErrorMessage();
                                                                        error.setApiVersion(apiVersion);
                                                                        error.setReturnCode("150001");
                                                                        error.setMessage("Invalid JSON format: " + e.getMessage());
                                                                        return complete(StatusCodes.BAD_REQUEST, error, marshaller);
                                                                    }
                                                                })
                                                        )
                                                ),

                                                // DELETE /api/tracking-details/{trackingNumber}
                                                path(PathMatchers.segment(), trackingNumber ->
                                                        delete(() -> {
                                                            log.info("Processing DELETE request for tracking number: {}", trackingNumber);
                                                            // Convert Scala Future to Java CompletionStage
                                                            CompletionStage<Object> future = FutureConverters.asJava(ask(trackingDetailRouter, new TrackingDetailMessages.DeleteTrackingDetail(trackingNumber), 5000));
                                                            return onSuccess(future, response -> {
                                                                if (!(response instanceof TrackingDetailMessages.TrackingDetailResponse)) {
                                                                    log.error("Unexpected response type: {}", response.getClass().getName());
                                                                    ErrorMessage error = new ErrorMessage();
                                                                    error.setApiVersion(apiVersion);
                                                                    error.setReturnCode("150000");
                                                                    error.setMessage("Unexpected response from actor");
                                                                    return complete(StatusCodes.INTERNAL_SERVER_ERROR, error, marshaller);
                                                                }
                                                                TrackingDetailMessages.TrackingDetailResponse resp = (TrackingDetailMessages.TrackingDetailResponse) response;
                                                                if (resp.getError() != null) {
                                                                    log.warn("Delete failed for tracking number: {}", trackingNumber);
                                                                    return complete(StatusCodes.NOT_FOUND, resp.getError(), marshaller);
                                                                }
                                                                log.info("Deleted tracking detail: {}", trackingNumber);
                                                                return complete(StatusCodes.NO_CONTENT);
                                                            });
                                                        })
                                                )
                                        )
                                )
                        )
                )
        );
    }
}