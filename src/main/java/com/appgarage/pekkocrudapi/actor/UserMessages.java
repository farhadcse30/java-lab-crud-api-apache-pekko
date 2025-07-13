package com.appgarage.pekkocrudapi.actor;

import com.appgarage.pekkocrudapi.model.User;
import java.io.Serializable;
import java.util.List;

/**
 * Message classes for Pekko actor communication for User operations.
 */
public class UserMessages {

    public static class CreateUser implements Serializable {
        private final User user;

        public CreateUser(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }
    }

    public static class GetUser implements Serializable {
        private final String id;

        public GetUser(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    public static class UpdateUser implements Serializable {
        private final String id;
        private final User user;

        public UpdateUser(String id, User user) {
            this.id = id;
            this.user = user;
        }

        public String getId() {
            return id;
        }

        public User getUser() {
            return user;
        }
    }

    public static class DeleteUser implements Serializable {
        private final String id;

        public DeleteUser(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    public static class GetAllUsers implements Serializable {
        // No fields needed for this message
    }

    public static class UserResponse implements Serializable {
        private final User user;
        private final String error;

        public UserResponse(User user, String error) {
            this.user = user;
            this.error = error;
        }

        public User getUser() {
            return user;
        }

        public String getError() {
            return error;
        }
    }

    public static class UserListResponse implements Serializable {
        private final List<User> users;
        private final String error;

        public UserListResponse(List<User> users, String error) {
            this.users = users;
            this.error = error;
        }

        public List<User> getUsers() {
            return users;
        }

        public String getError() {
            return error;
        }
    }
}