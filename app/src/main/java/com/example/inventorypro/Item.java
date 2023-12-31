package com.example.inventorypro;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Primitive Item object. Automatically serializable/deserializable by Firestore and also can be passed via intent.
 */
public class Item implements Parcelable {
    private String name;
    private Double value;
    private Long date; // Allows date to be serialized and deserialized by Firestore (also easier to query database).
    private String make; // uid reference to make.
    private String model;

    // This is String type so it can be any length, and sometimes serial numbers
    // contain characters.
    private String serialNumber;
    private String description;
    private String comment;

    private List<String> tags; // uid reference to tag
    private List<String> imageUris;
    private String uid;

    private boolean selected; // TODO: this shouldn't be here ideally.

    // TODO: images
    // TODO: tags

    public Item(){
        // Firestore:
        // Each custom class must have a public constructor that takes no arguments.
        // In addition, the class must include a public getter for each property.
    }

    /**
     * Constructor
     * @param name the name of the item
     * @param value the value of the item
     * @param date the date of purchase or acquisition of the item
     * @param make the make of the item
     * @param serialNumber the serial number of the item
     * @param description the description of the item
     * @param comment a comment for the item
     * @param uid a unique id for the item (generate a new one with Item.generateNewUID())
     */
    public Item(String name,
                Double value,
                LocalDate date,
                String make,
                String model,
                String serialNumber,
                String description,
                String comment,
                List<String> tags,
                List<String> imageUris,
                String uid) {
        this.name = name;
        this.value = value;
        setLocalDate(date);
        this.make = make;
        this.model = model;
        this.serialNumber = serialNumber;
        this.description = description;
        this.comment = comment;
        if (tags == null){
            this.tags = Arrays.asList();
        }else{
            this.tags= tags;
        }
        this.imageUris = imageUris;
        this.uid = uid;
    }

    protected Item(Parcel in) {
        name = in.readString();
        if (in.readByte() == 0) {
            value = null;
        } else {
            value = in.readDouble();
        }
        if (in.readByte() == 0) {
            date = null;
        } else {
            date = in.readLong();
        }
        make = in.readString();
        model = in.readString();
        serialNumber = in.readString();
        description = in.readString();
        comment = in.readString();

        this.tags = in.createStringArrayList();
        this.imageUris = in.createStringArrayList();
        this.uid = in.readString();
    }

    /**
     * Required to be able to pass the item between activities using intents.
     */
    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Gets the date as a long (used by firestore serialization).
     * @return Returns the date as a long.
     */
    public Long getDate() {
        return date;
    }

    /**
     * Sets the date using a long.
     * @param date The date as a long to set to.
     */
    public void setDate(Long date) {
        this.date = date;
    }

    /**
     * Gets the date as a LocalDate object (implicit conversion of long to date object).
     * @return The LocalDate that this item is assigned.
     */
    @Exclude
    public LocalDate getLocalDate() {
        if(this.date == null){
            return null;
        }
        return Instant.ofEpochMilli(date)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * Sets the date given a LocalDate object (implicit conversion of date to long for storage).
     */
    @Exclude
    public void setLocalDate(LocalDate date) {
        if(date == null) {
            this.date = null;
            return;
        }
        this.date = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public String getMake() {
        return make;
    }
    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Returns true if this item is selected.
     * @return
     */
    @Exclude
    public boolean isSelected() {
        return selected;
    }

    /**
     * Set the selected flag (ignored by the database).
     * @param selected
     */
    @Exclude
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public List<String> getTags() {
        return tags;
    }
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     * Returns a string that is the concatenation of all the tags on a single line.
     * @return
     */
    public String tagRepresentation(){
        String ss = "";
        for(String s : tags){
            ss+=s;
        }
        return ss;
    }
    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }
    public void removeTag(String tag) {
        if (tags.contains(tag)) {
            tags.remove(tag);
        }
    }

    /**
     * Returns true if the tag exists in this item (WIP).
     * @param tag
     * @return
     */
    public boolean hasTag(String tag){
        return tags.contains(tag);
    }

    /**
     * Gets the unique identifier that identifies this item.
     * @return The unique identifier that identifies this item (currently the name, might be changed to UUID later).
     */
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

    public static String generateNewUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Required to pass this item by intent between activities.
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Used to pass the objects between activities using intents.
     * @param dest The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     * May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        if (value == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(value);
        }
        if (date == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(date);
        }
        dest.writeString(make);
        dest.writeString(model);
        dest.writeString(serialNumber);
        dest.writeString(description);
        dest.writeString(comment);
        dest.writeStringList(this.tags);
        dest.writeStringList(this.imageUris);
        dest.writeString(uid);
    }

    public List<String> getImageUris() {
        if(imageUris ==null){
            return new ArrayList<>();
        }
        return imageUris;
    }

    /**
     * Replaces a URI (used when updating a local path to firestore path).
     * @param old The old URI.
     * @param newUri The new URI.
     */
    public void replaceUri(String old, String newUri){
        int i = imageUris.indexOf(old);
        if(i==-1)return;
        imageUris.set(i, newUri);
    }
    public void deleteUri(String uri){
        imageUris.remove(uri);
    }
}