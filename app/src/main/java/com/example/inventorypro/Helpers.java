package com.example.inventorypro;

import static java.lang.Integer.parseInt;

import java.time.LocalDate;

/**
 * A class containing helper functions which are used throughout the application.
 */
public class Helpers {
    public static LocalDate parseDate(String text){
        return LocalDate.of(parseInt(text.substring(0,4)),parseInt(text.toString().substring(5,7)),parseInt(text.substring(8,10)));
    }
}
