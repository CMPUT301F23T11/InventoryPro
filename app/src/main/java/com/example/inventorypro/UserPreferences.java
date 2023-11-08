package com.example.inventorypro;

/**
 * Stores user preferences for the whole user lifecycle. Does not persist between closures.
 * Created at the point that the user is logged in.
 */
public class UserPreferences {
    private static UserPreferences instance = null;
    public static UserPreferences getInstance(){
        return instance;
    }
    public static void createInstance(String userID){
        instance = new UserPreferences(userID,new SortSettings(),new FilterSettings());
    }

    private UserPreferences(String userID, SortSettings ss, FilterSettings fs){
        this.userID = userID;
        sortSettings = ss;
        filterSettings = fs;
    }

    private String userID;
    private SortSettings sortSettings;
    private FilterSettings filterSettings;

    public SortSettings getSortSettings() {
        return sortSettings;
    }

    public FilterSettings getFilterSettings() {
        return filterSettings;
    }

    public String getUserID() {
        return userID;
    }
}
