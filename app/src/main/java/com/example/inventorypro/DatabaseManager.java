package com.example.inventorypro;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Responsible for communication with Firestore as well as invoking updates to the application in relation to this data.
 */
public class DatabaseManager {
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference userImagesStorageReference;

    private Boolean isConnected;
    private String userUID;

    public DatabaseManager(){
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
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

        // Create a storage reference from our app
        userImagesStorageReference = storage.getReference().child(getStorageImagesPath());

        isConnected = Boolean.TRUE;
    }

    public String uploadImage(@NonNull Item item, @NonNull Uri image){
        Log.e(TAG, "uploadImage: "+ image.toString());

        StorageReference ref = userImagesStorageReference.child(image.getLastPathSegment()+
                UUID.randomUUID().toString().replace("-",""));
        UploadTask uploadTask = ref.putFile(image);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e("GAN",exception.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                Log.e("GAN",ref.getPath());
            }
        });

        return ref.getPath();
    }
    public void deleteImage(@NonNull Item item, @NonNull Uri image){
        // Create a reference to the file to delete
        StorageReference desertRef = storage.getReference(image.toString());

        // Delete the file
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.d(TAG, "Successfully deleted image " +image);
                item.deleteUri(image.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "Failed to delete image " +image);
            }
        });
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

        for(String s : item.getImageUris()){
            Uri uri = Uri.parse(s);
            if(uri==null)continue;
            Log.e(TAG, "consider to upload "+uri.toString() );
            if(uri.toString().startsWith("/Users/")) continue;
            String newUri = uploadImage(item,uri);
            Log.e(TAG, "replaced with "+newUri );
            item.replaceUri(s,newUri);
        }

        db.document(getDBItemPath(item.getUid())).set(item);

        Log.d(TAG,"Adding Item: "+item.getUid());
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
    /**
     * Removes an item from the database.
     * @param item The item to remove.
     */
    public void removeItem(Item item, boolean deepDelete){
        if(!isConnected){
            throw new RuntimeException("Database is not connected.");
        }
        if(item==null){
            throw new IllegalArgumentException("Item was null.");
        }

        db.document(getDBItemPath(item.getUid())).delete();
        if (deepDelete){
            for(String s : item.getImageUris()){
                Uri uri = Uri.parse(s);
                if(uri==null)continue;
                deleteImage(item,uri);
            }
        }

        Log.d(TAG,"Deleting Item: "+item.getUid());
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

    private String getStorageImagesPath(){
        return String.format("Users/%s/Images",userUID);
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

    /**
     * Downloads the image from firebase and displays it async into an image view.
     * @param context The context.
     * @param imageView The image view to display into.
     * @param uri The image URI (on item).
     */
    public static void downloadAndDisplayImageAsync(Context context, ImageView imageView, @NonNull String uri){
        final StorageReference ref = FirebaseStorage.getInstance().getReference(uri);

        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Download directly from StorageReference using Glide
                Glide.with(context)
                        .load(uri)
                        .placeholder(R.drawable.baseline_downloading)
                        .into(imageView);
                imageView.setBackground(null);
            }
        });
    }

    public static void getImageUris(Context context, List<String> uris, final OnSuccessListener<List<Uri>> onSuccessListener) {
        List<Uri> imageUris = new ArrayList<>();
        AtomicInteger count = new AtomicInteger(uris.size());

        for (String uri : uris) {
            StorageReference ref = FirebaseStorage.getInstance().getReference(uri);

            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    imageUris.add(uri);
                    if (count.decrementAndGet() == 0) {
                        // All URIs have been fetched, invoke the listener
                        onSuccessListener.onSuccess(imageUris);
                    }
                }
            });
        }
    }


    final static String TAG = "Firestore";
}
