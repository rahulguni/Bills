package com.example.bills.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class BillItem implements Parcelable {
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

    protected BillItem(Parcel in) {
        billItemId = in.readString();
        itemName = in.readString();
        itemPrice = in.readDouble();
        itemQuantity = in.readInt();
        users = in.createStringArrayList();
    }

    public static final Creator<BillItem> CREATOR = new Creator<BillItem>() {
        @Override
        public BillItem createFromParcel(Parcel in) {
            return new BillItem(in);
        }

        @Override
        public BillItem[] newArray(int size) {
            return new BillItem[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(billItemId);
        parcel.writeString(itemName);
        parcel.writeDouble(itemPrice);
        parcel.writeInt(itemQuantity);
        parcel.writeStringList(users);
    }

    @Override
    public String toString() {
        return "BillItem{" +
                "billItemId='" + billItemId + '\'' +
                ", itemName='" + itemName + '\'' +
                ", itemPrice=" + itemPrice +
                ", itemQuantity=" + itemQuantity +
                ", users=" + users +
                '}';
    }
}
