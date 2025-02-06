package org.example.models;

public class Session {
    private static User currentUser;

    public static User getUser() {
        return currentUser;
    }

    public static void setUser(User user) {
        currentUser = user;
    }


    public static boolean isLoggedIn() {
        return currentUser != null;
    }


    public static int getUserId() {
        if (currentUser != null) {
            return currentUser.getId();
        }
        return -1;
    }
}
