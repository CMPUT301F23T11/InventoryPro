package com.example.inventorypro;

/**
 * Stores user preferences for the whole user lifecycle. Does not persist between closures.
 * Created at the point that the user is logged in.
 * Uses Singleton pattern.
 */
public class User {
    private static User instance = null;
    public static User getInstance(){
        return instance;
    }
    public static Boolean hasUser(){return instance != null;}

    /**
     * Create UserPreferences instance using the user ID (google auth id).
     * @param userID The user ID to use.
     */
    public static void createInstance(String userID){
        instance = new User(userID,new SortSettings(),new FilterSettings());
        setupUser();
    }
    public static void setupUser(){
        DatabaseManager database = new DatabaseManager();

        ItemList itemList = new ItemList(database);
        ItemList.setInstance(itemList);

        TagList tagList = new TagList(database);
        TagList.setInstance(tagList);

        database.connect(User.getInstance().getUserID(), itemList,tagList);
    }

    private User(String userID, SortSettings ss, FilterSettings fs){
        this.userID = userID;
        sortSettings = ss;
        filterSettings = fs;
    }

    private String userID = null;
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
