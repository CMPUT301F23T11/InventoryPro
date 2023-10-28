package com.example.inventorypro;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class DatabaseManager {

    final static String TAG = "Firestore";
    final static String ITEMS = "Items";

    private FirebaseFirestore db;
    private CollectionReference userRef;

    private Boolean isConnected;

    public DatabaseManager(){
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Connect to firebase using the user ID (probably ID from Google authentication).
     * @param userID Unique identifier for each user.
     */
    public void connect(String userID, ItemList itemList){
        userRef = db.collection(userID);

        userRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots,
                                @Nullable FirebaseFirestoreException error) {
                onSnapshot(itemList,querySnapshots,error);
            }
        });

        isConnected = Boolean.TRUE;
    }

    public void addItem(Item item){
        // Each custom class must have a public constructor that takes no arguments.
        // In addition, the class must include a public getter for each property.
        userRef.document(ITEMS).collection(item.getName()).add(item);
        Log.e(TAG,"Adding Item: "+item.getName());
    }

    /**
     * Automatically called whenever the user's document is changed (immediately gets called for local changes too).
     * Synchronizes the itemList with the database data.
     * @param itemList The item list to update (hooked in the connect() method).
     * @param querySnapshots The real-time snapshot for the user's document.
     * @param error Any error associated with fetching the snapshot.
     */
    private void onSnapshot(ItemList itemList, @Nullable QuerySnapshot querySnapshots,
                            @Nullable FirebaseFirestoreException error){
        if (error != null) {
            Log.e(TAG, error.toString());
            return;
        }
        if(querySnapshots==null){
            return;
        }

        // This method will (almost) immediately fire even when the changes are still pending.
        // We can differentiate between document changes that are due to local/server adjustments.
        String source = querySnapshots.getMetadata().hasPendingWrites()
                ? "Local" : "Server";

        Log.e(TAG,"Processing Snapshots "+querySnapshots.getMetadata().toString());
        for (QueryDocumentSnapshot doc: querySnapshots) {
            // Doesn't fetch anything for some reason????
            Log.e(TAG,"Fetched: "+doc.getId());
        }
    }

    public Boolean getConnected() {
        return isConnected;
    }

}
