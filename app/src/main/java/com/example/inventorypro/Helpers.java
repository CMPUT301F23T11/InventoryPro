package com.example.inventorypro;

import static java.lang.Integer.parseInt;

import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class containing helper functions which are used throughout the application.
 */
public class Helpers {

    /**
     * Parse the date from text.
     * @param text The text representing the date.
     * @return The date (nullable for invalid text).
     */
    @Nullable
    public static LocalDate parseDate(String text){
        if(!isValidDate(text)) return null;
        return LocalDate.of(parseInt(text.substring(0,4)),parseInt(text.toString().substring(5,7)),parseInt(text.substring(8,10)));
    }

    /**
     * Whether or not the text would parse a valid date.
     * @param text The text to test.
     * @return Returns true if the text would be parsed to a date successfully.
     */
    public static boolean isValidDate(String text){
        if(text.length() == 0){

            return false;
        } else if(text.length() != 10){

            return false;
            //check for month
        } else if(parseInt(text.substring(5,7))>12 || parseInt(text.substring(5,7))<1) {

            return false;
            //validate day
        } else if(parseInt(text.substring(8))<1 || parseInt(text.substring(8)) > 31) {

            return false;
        } else if(parseInt(text.substring(8))<1 || parseInt(text.substring(8)) > 30) {
            List<Integer> thirtyDaymonths = new ArrayList<>(Arrays.asList(4,6,9,11));
            if (thirtyDaymonths.contains(parseInt(text.substring(5,7)))){

                return false;
            }
        } else if(parseInt(text.substring(8))<1 || parseInt(text.substring(8)) > 29) {

            if (parseInt(text.substring(5,7)) == 2){

                return false;
            }
        }

        return true;
    }
}
