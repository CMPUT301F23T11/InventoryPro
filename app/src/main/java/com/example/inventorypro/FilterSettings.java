package com.example.inventorypro;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.util.ArrayList;

public class FilterSettings {

    private LocalDate from, to;
    private ArrayList<String> keywords;
    private ArrayList<String> tags;
    private ArrayList<String> makes; // TODO: this being a string doesn't make much sense (why shouldnt tag be a string)

    public FilterSettings(LocalDate from, LocalDate to, ArrayList<String> keywords, ArrayList<String> tags, ArrayList<String> makes) {
        this.from = from;
        this.to = to;
        this.keywords = keywords;
        this.tags = tags;
        this.makes = makes;
    }

    public FilterSettings() {
        this(null,null,null,null,null);
    }

    public Boolean itemSatisfiesFilter(@NonNull Item item){
        if(from != null && item.getLocalDate().isBefore(from)){
            return Boolean.FALSE;
        }
        if(to != null && item.getLocalDate().isAfter(to)){
            return Boolean.FALSE;
        }
        if(keywords != null){
            Boolean hasOne = Boolean.FALSE;
            for (String w : keywords){
                if(item.getDescription().toLowerCase().contains(w.toLowerCase())){
                    hasOne = Boolean.TRUE;
                    break;
                }
            }
            if(!hasOne) return Boolean.FALSE;
        }
        if(tags != null){
            Boolean hasOne = Boolean.FALSE;
            for (String w : tags){
                if(item.hasTag(w)){
                    hasOne = Boolean.TRUE;
                    break;
                }
            }
            if(!hasOne) return Boolean.FALSE;
        }
        if(makes != null){
            Boolean hasOne = Boolean.FALSE;
            for (String w : makes){
                if(item.getMake().equals(w)){
                    hasOne = Boolean.TRUE;
                    break;
                }
            }
            if(!hasOne) return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }
}
