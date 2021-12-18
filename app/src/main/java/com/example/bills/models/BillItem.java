package com.example.bills.models;

import java.util.ArrayList;

public class BillItem {
    private String billItemId;
    private String itemName;
    private double itemPrice;
    private int itemQuantity;
    private ArrayList<String> users;

    public BillItem(String billItemId, String itemName, double itemPrice, int itemQuantity) {
        this.billItemId = billItemId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemQuantity = itemQuantity;
        this.users = new ArrayList<>();
    }

    public String getBillItemId() {
        return billItemId;
    }

    public void setBillItemId(String billItemId) {
        this.billItemId = billItemId;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

}
