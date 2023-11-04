package com.example.inventorypro;

import java.time.LocalDate;
import java.util.ArrayList;

public class FilterSettings {

    private LocalDate from, to;
    private ArrayList<String> keywords;
    private ArrayList<Tag> tags;
    private ArrayList<Make> makes;

    public FilterSettings(LocalDate from, LocalDate to, ArrayList<String> keywords, ArrayList<Tag> tags, ArrayList<Make> makes) {
        this.from = from;
        this.to = to;
        this.keywords = keywords;
        this.tags = tags;
        this.makes = makes;
    }

    public FilterSettings() {
        this(null,null,null,null,null);
    }
}
