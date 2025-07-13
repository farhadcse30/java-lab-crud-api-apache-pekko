package com.appgarage.pekkocrudapi.config;

import org.apache.pekko.actor.ActorSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Pekko actor system.
 */
@Configuration
public class PekkoConfig {

    @Bean
    public ActorSystem actorSystem() {
        // Create Pekko actor system with default configuration
        return ActorSystem.create("ProductActorSystem");
    }
}