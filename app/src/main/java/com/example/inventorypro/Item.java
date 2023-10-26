package com.example.inventorypro;

import java.math.BigDecimal;
import java.util.Date;

public class Item {
    private String name;
    private BigDecimal value;
    private Date date;
    private String make;

    // This is String type so it can be any length, and sometimes serial numbers
    // contain characters.
    private String serialNumber;
    private String description;
    private String comment;

    // TODO - images
    // TODO - tags
    // TODO - database management

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
                String value,
                Date date,
                String make,
                String serialNumber,
                String description,
                String comment) {
        this.name = name;
        this.value = new BigDecimal(value);
        this.make = make;
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

    public BigDecimal getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = new BigDecimal(value);
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public String getMake() {
        return make;
    }
    public void setMake(String make) {
        this.make = make;
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
