package com.appgarage.pekkocrudapi;

import com.appgarage.pekkocrudapi.routes.TrackingDetailApiRoute;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.http.javadsl.Http;
import org.apache.pekko.http.javadsl.ServerBinding;
import org.apache.pekko.routing.RoundRobinPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.CompletionStage;

// Spring Boot application class to bootstrap the Pekko HTTP server and Actor System
@SpringBootApplication
public class PekkoCrudApiApplication {

    @Value("${server.port.pekko:8088}")
    private int pekkoPort;

    public static void main(String[] args) {
        SpringApplication.run(PekkoCrudApiApplication.class, args);
    }

    @Bean
    public ActorRef trackingDetailRouter(ActorSystem actorSystem, @Value("${api.version}") String apiVersion) {
        return actorSystem.actorOf(
                new RoundRobinPool(5).props(com.appgarage.pekkocrudapi.actor.TrackingDetailActor.props(apiVersion)),
                "trackingDetailRouter"
        );
    }

    @Bean
    public TrackingDetailApiRoute trackingDetailApiRoute(ActorSystem actorSystem, @Value("${api.version}") String apiVersion, ActorRef trackingDetailRouter, com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        return new TrackingDetailApiRoute(actorSystem, apiVersion, trackingDetailRouter, objectMapper);
    }

    @Bean
    public CompletionStage<ServerBinding> pekkoHttpServer(ActorSystem actorSystem, TrackingDetailApiRoute route) {
        return Http.get(actorSystem)
                .newServerAt("localhost", pekkoPort)
                .bind(route.createRoute());
    }
}
