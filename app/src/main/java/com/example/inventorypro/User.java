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

    /**
     * Creates empty user (disconnected from database).
     */
    public static void setEmptyInstance()
    {
        instance =  new User(
                null, null, null, new SortSettings(), new FilterSettings());
    }
    public static Boolean hasUser(){return instance != null;}

    /**
     * Create UserPreferences instance using the user ID (google auth id).
     * @param userID The user ID to use.
     */
    public static void createInstance(String userID, String emailID, String displayName){
        instance = new User(
                userID, emailID, displayName, new SortSettings(), new FilterSettings());
        setupUser();
    }

    /**
     * Instantiate all singletons/data/connections required for the new user being created.
     */
    private static void setupUser(){
        DatabaseManager database = new DatabaseManager();

        ItemList itemList = new ItemList(database);
        ItemList.setInstance(itemList);

        TagList tagList = new TagList(database);
        TagList.setInstance(tagList);

        database.connect(User.getInstance().getUserID(), itemList,tagList);
    }

    private User(
            String userID, String emailID, String displayName, SortSettings ss, FilterSettings fs)
    {
        this.userID = userID;
        this.emailID = emailID;
        this.displayName = displayName;
        sortSettings = ss;
        filterSettings = fs;
    }

    private String userID = null;
    private String emailID = null;
    private String displayName = null;
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

    public String getEmailID() {
        return emailID;
    }

    public String getDisplayName() {
        return displayName;
    }
}
