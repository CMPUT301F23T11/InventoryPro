package com.example.inventorypro;

import com.google.firebase.firestore.Exclude;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;

public class Item {
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
        // this.date = date;
        setLocalDate(date);
        this.make = make;
        this.model = model;
        this.serialNumber = serialNumber;
        this.description = description;
        this.comment = comment;
    }

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
        return Instant.ofEpochMilli(date)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
    @Exclude
    public void setLocalDate(LocalDate date) {
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
}
