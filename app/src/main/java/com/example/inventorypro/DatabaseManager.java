package com.example.inventorypro;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for communication with Firestore as well as invoking updates to the application in relation to this data.
 */
public class DatabaseManager {
    private FirebaseFirestore db;

    private Boolean isConnected;
    private String userUID;

    public DatabaseManager(){
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Connect to firebase using the user ID (usually ID from Google authentication).
     * @param userID Unique identifier for each user.
     * @param itemList The itemList to synchronize with.
     */
    public void connect(@NonNull String userID, @NonNull ItemList itemList, @NonNull TagList tagList){
        this.userUID = userID;

        // Create various snapshot listeners depending on what we want to listen to.
        db.collection(getDBItemsCollectionPath()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                onItemsSnapshot(itemList,value,error);
            }
        });
        db.collection(getDBTagsCollectionPath()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                onTagsSnapshot(tagList,value,error);
            }
        });

        isConnected = Boolean.TRUE;
    }

    /**
     * Adds an item to the database. Overwrites the item or adds it if it's new.
     * @param item The item to add.
     */
    public void addItem(Item item){
        if(!isConnected){
            throw new RuntimeException("Database is not connected.");
        }
        if(item==null){
            throw new IllegalArgumentException("Item was null.");
        }

        db.document(getDBItemPath(item.getUID())).set(item);

        Log.d(TAG,"Adding Item: "+item.getUID());
    }
    public void addTag(String item){
        if(!isConnected){
            throw new RuntimeException("Database is not connected.");
        }
        if(item==null){
            throw new IllegalArgumentException("Item was null.");
        }

        db.document(getDBTagPath(item)).set(new HashMap<String,Object>());
        Log.d(TAG,"Adding Item: "+item);
    }
    public void replaceItem(Item oldItem, Item newItem){
        if (!isConnected) {
            throw new RuntimeException("Database is not connected.");
        }

        if (oldItem == null || newItem == null) {
            throw new IllegalArgumentException("Both oldItem and newItem must be non-null.");
        }

        // Check if the oldItem exists in the database and replace it with newItem
        db.document(getDBItemPath(oldItem.getUID())).set(newItem);
        Log.d(TAG, "Replacing Item: " + oldItem.getUID() + " with " + newItem.getUID());

    }
    /**
     * Removes an item from the database.
     * @param item The item to remove.
     */
    public void removeItem(Item item){
        if(!isConnected){
            throw new RuntimeException("Database is not connected.");
        }
        if(item==null){
            throw new IllegalArgumentException("Item was null.");
        }

        db.document(getDBItemPath(item.getUID())).delete();
        Log.d(TAG,"Deleting Item: "+item.getUID());
    }
    public void removeTag(String item){
        if(!isConnected){
            throw new RuntimeException("Database is not connected.");
        }
        if(item==null){
            throw new IllegalArgumentException("Item was null.");
        }

        db.document(getDBTagPath(item)).delete();
        Log.d(TAG,"Deleting Item: "+item);
    }

    /**
     * Called whenever the users Tags collection is updated.
     * @param tagList The tag list to synchronize with (hooked in the connect() method).
     * @param querySnapshots The real-time snapshot for the Tags.
     * @param error Any error associated with fetching the snapshots.
     */
    private void onTagsSnapshot(TagList tagList, @Nullable QuerySnapshot querySnapshots,
                                 @Nullable FirebaseFirestoreException error){
        if (error != null) {
            Log.e(TAG, error.toString());
            return;
        }
        if(querySnapshots==null){
            return;
        }

        // Each document fetched from the collection here is an Tag.
        // Regenerate the tags list.
        Log.d(TAG,"Processing Snapshots Tags "+querySnapshots.getMetadata().toString());
        ArrayList<String> tags = new ArrayList<>();
        for(DocumentSnapshot s : querySnapshots.getDocuments()){
            Log.d(TAG,"Found Tag: "+s.getId());
            String tag = s.getId();
            if(tag == null){
                Log.e(TAG,"Failed to parse tag from database: "+s.getId());
                continue;
            }
            Log.d(TAG,"Parsed Tag: "+tag);
            tags.add(tag);
        }

        // Synchronize the itemList with the items.
        tagList.onSynchronize(tags);
    }

    /**
     * Called whenever the users Items collection is updated.
     * @param itemList The item list to synchronize with (hooked in the connect() method).
     * @param querySnapshots The real-time snapshot for the Items.
     * @param error Any error associated with fetching the snapshots.
     */
    private void onItemsSnapshot(ItemList itemList, @Nullable QuerySnapshot querySnapshots,
                            @Nullable FirebaseFirestoreException error){
        if (error != null) {
            Log.e(TAG, error.toString());
            return;
        }
        if(querySnapshots==null){
            return;
        }

        // Each document fetched from the collection here is an Item.
        // Regenerate the items list.
        Log.d(TAG,"Processing Snapshots "+querySnapshots.getMetadata().toString());
        ArrayList<Item> items = new ArrayList<>();
        for(DocumentSnapshot s : querySnapshots.getDocuments()){
            Log.d(TAG,"Found Item: "+s.getId());
            Item item = s.toObject(Item.class);
            if(item == null){
                Log.e(TAG,"Failed to parse item from database: "+s.getId());
                continue;
            }
            Log.d(TAG,"Parsed Item: "+item.getName());
            items.add(item);
        }

        // Synchronize the itemList with the items.
        itemList.onSynchronize(items);
    }

    /**
     * Fetch the database path for a users defined tags.
     * @return The path considering the user ID.
     */
    private String getDBTagsCollectionPath(){
        return String.format("Users/%s/Tags", userUID);
    }
    /**
     * Fetch the database path for a users Items.
     * @return The path considering the userID.
     */
    private String getDBItemsCollectionPath(){
        return String.format("Users/%s/Items", userUID);
    }
    /**
     * Fetch the database path for a user's item.
     * @param itemUID The item uid.
     * @return The path considering the userID + itemID.
     */
    private String getDBItemPath(String itemUID){
        return String.format("Users/%s/Items/%s", userUID, itemUID);
    }

    private String getDBTagPath(String itemUID){
        return String.format("Users/%s/Tags/%s", userUID, itemUID);
    }

    /**
     * Returns true if this database manager has been properly configured to be used.
     * @return
     */
    public Boolean getConnected() {
        return isConnected;
    }

    final static String TAG = "Firestore";
}
