package com.appgarage.pekkocrudapi.service;

import com.appgarage.pekkocrudapi.actor.UserActor;
import com.appgarage.pekkocrudapi.actor.UserMessages;
import com.appgarage.pekkocrudapi.model.User;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.routing.RoundRobinPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import static org.apache.pekko.pattern.PatternsCS.ask;

/**
 * Service layer to interact with Pekko actors for User operations.
 * Uses RoundRobinPool for load balancing across multiple actor instances.
 */
@Service
public class UserService {

    private final ActorRef userRouter;

    @Autowired
    public UserService(ActorSystem actorSystem) {
        // Create a router with 5 actor instances for concurrency
        this.userRouter = actorSystem.actorOf(
                new RoundRobinPool(5).props(UserActor.props()),
                "userRouter"
        );
    }

    public CompletableFuture<UserMessages.UserResponse> createUser(User user) {
        return ask(userRouter, new UserMessages.CreateUser(user), 5000)
                .toCompletableFuture()
                .thenApply(response -> (UserMessages.UserResponse) response);
    }

    public CompletableFuture<UserMessages.UserResponse> getUser(String id) {
        return ask(userRouter, new UserMessages.GetUser(id), 5000)
                .toCompletableFuture()
                .thenApply(response -> (UserMessages.UserResponse) response);
    }

    public CompletableFuture<UserMessages.UserResponse> updateUser(String id, User user) {
        return ask(userRouter, new UserMessages.UpdateUser(id, user), 5000)
                .toCompletableFuture()
                .thenApply(response -> (UserMessages.UserResponse) response);
    }

    public CompletableFuture<UserMessages.UserResponse> deleteUser(String id) {
        return ask(userRouter, new UserMessages.DeleteUser(id), 5000)
                .toCompletableFuture()
                .thenApply(response -> (UserMessages.UserResponse) response);
    }

    public CompletableFuture<UserMessages.UserListResponse> getAllUsers() {
        return ask(userRouter, new UserMessages.GetAllUsers(), 5000)
                .toCompletableFuture()
                .thenApply(response -> (UserMessages.UserListResponse) response);
    }
}