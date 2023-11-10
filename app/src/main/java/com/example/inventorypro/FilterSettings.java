package com.example.inventorypro;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * The FilterSettings contain all of the settings data required to perform filtering on the item list.
 */
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

    /**
     * Clears the filter settings back to default.
     */
    public void clear(){
        from = null;
        to = null;
        keywords = null;
        tags = null;
        makes = null;
    }

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public ArrayList<String> getMakes() {
        return makes;
    }

    public void setMakes(ArrayList<String> makes) {
        this.makes = makes;
    }

    /**
     * Returns whether or not an item satisfies this filter.
     * @param item The item to test.
     * @return True if the item passes this filter (satisfies the configuration of this filter).
     */
    public Boolean itemSatisfiesFilter(@NonNull Item item){
        if(from != null && item.getLocalDate().isBefore(from)){
            return Boolean.FALSE;
        }
        if(to != null && item.getLocalDate().isAfter(to)){
            return Boolean.FALSE;
        }
        if(keywords != null && keywords.size() > 0){
            Boolean hasOne = Boolean.FALSE;
            for (String w : keywords){
                if(item.getDescription().toLowerCase().contains(w.toLowerCase())){
                    hasOne = Boolean.TRUE;
                    break;
                }
            }
            if(!hasOne) return Boolean.FALSE;
        }
        if(tags != null && tags.size() > 0){
            Boolean hasOne = Boolean.FALSE;
            for (String w : tags){
                if(item.hasTag(w)){
                    hasOne = Boolean.TRUE;
                    break;
                }
            }
            if(!hasOne) return Boolean.FALSE;
        }
        if(makes != null&& makes.size()>0){
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
