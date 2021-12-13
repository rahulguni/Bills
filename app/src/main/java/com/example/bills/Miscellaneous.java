package com.example.bills;

//For Miscellaneous Functions
public class Miscellaneous {
    public boolean checkTextFields(String[] fields) {
        for(int i = 0; i < fields.length; i++) {
            if(fields[i].isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public String replaceWhiteSpace(String word) {
        String newString = word.replaceAll(" ", "%");
        return newString;
    }

    public String replacePercentage(String word) {
        String newString = word.replaceAll("%", " ");
        return newString;
    }

}
