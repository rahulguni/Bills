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

}