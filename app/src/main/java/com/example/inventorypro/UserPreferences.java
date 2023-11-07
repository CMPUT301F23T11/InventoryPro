package com.example.inventorypro;

/**
 * Stores user preferences for the whole application lifecycle. Does not persist.
 */
public class UserPreferences {
    private static UserPreferences instance = null;
    public static UserPreferences getInstance(){
        return instance;
    }
    public static void createInstance(){
        instance = new UserPreferences();
    }

    private String userID;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
