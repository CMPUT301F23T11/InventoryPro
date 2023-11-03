package com.example.inventorypro;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;

public class Item implements Parcelable {
    private String name;
    private Double value;
    private Long date; // Allows date to be serialized and deserialized by Firestore (also easier to query database).
    private String make;
    private String model;

    // This is String type so it can be any length, and sometimes serial numbers
    // contain characters.
    private String serialNumber;
    private String description;
    private String comment;

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
     */
    public Item(String name,
                Double value,
                LocalDate date,
                String make,
                String model,
                String serialNumber,
                String description,
                String comment) {
        this.name = name;
        this.value = value;
        setLocalDate(date);
        this.make = make;
        this.model = model;
        this.serialNumber = serialNumber;
        this.description = description;
        this.comment = comment;
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
    }

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

    public Long getDate() {
        return date;
    }
    public void setDate(Long date) {
        this.date = date;
    }

    @Exclude
    public LocalDate getLocalDate() {
        if(this.date == null){
            return null;
        }
        return Instant.ofEpochMilli(date)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
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
     * Gets the unique identifier that identifies this item.
     * @return The unique identifier that identifies this item (currently the name)
     */
    @Exclude
    public String getUID(){
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

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
    }
}
