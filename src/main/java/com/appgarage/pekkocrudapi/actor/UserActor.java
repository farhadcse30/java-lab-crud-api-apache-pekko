package com.appgarage.pekkocrudapi.actor;

import com.appgarage.pekkocrudapi.model.User;
import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.Props;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Actor responsible for handling User CRUD operations and retrieving all users.
 * Maintains an in-memory store for simplicity (replace with DB in production).
 */
public class UserActor extends AbstractActor {
    private final Map<String, User> userStore = new HashMap<>();

    public static Props props() {
        return Props.create(UserActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(UserMessages.CreateUser.class, this::handleCreate)
                .match(UserMessages.GetUser.class, this::handleGet)
                .match(UserMessages.UpdateUser.class, this::handleUpdate)
                .match(UserMessages.DeleteUser.class, this::handleDelete)
                .match(UserMessages.GetAllUsers.class, this::handleGetAll)
                .build();
    }

    private void handleCreate(UserMessages.CreateUser msg) {
        User user = msg.getUser();
        String id = UUID.randomUUID().toString();
        user.setId(id);
        userStore.put(id, user);
        sender().tell(new UserMessages.UserResponse(user, null), self());
    }

    private void handleGet(UserMessages.GetUser msg) {
        User user = userStore.get(msg.getId());
        String error = user == null ? "User not found" : null;
        sender().tell(new UserMessages.UserResponse(user, error), self());
    }

    private void handleUpdate(UserMessages.UpdateUser msg) {
        if (userStore.containsKey(msg.getId())) {
            User user = msg.getUser();
            user.setId(msg.getId());
            userStore.put(msg.getId(), user);
            sender().tell(new UserMessages.UserResponse(user, null), self());
        } else {
            sender().tell(new UserMessages.UserResponse(null, "User not found"), self());
        }
    }

    private void handleDelete(UserMessages.DeleteUser msg) {
        User user = userStore.remove(msg.getId());
        String error = user == null ? "User not found" : null;
        sender().tell(new UserMessages.UserResponse(user, error), self());
    }

    private void handleGetAll(UserMessages.GetAllUsers msg) {
        sender().tell(new UserMessages.UserListResponse(new ArrayList<>(userStore.values()), null), self());
    }
}
