package com.example.bills.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Bill implements Parcelable {
    private String billId;
    private double totalPrice;
    private boolean approved;
    private double tax;
    private long timestamp;
    ArrayList<BillItem> allItems;

    public Bill(String billId, double totalPrice, double tax, boolean approved, ArrayList<BillItem> allItems) {
        this.billId = billId;
        this.totalPrice = totalPrice;
        this.tax = tax;
        this.approved = approved;
        this.allItems = allItems;
        this.timestamp = System.currentTimeMillis()/1000;
    }

    public Bill(){};

    protected Bill(Parcel in) {
        billId = in.readString();
        totalPrice = in.readDouble();
        approved = in.readByte() != 0;
        tax = in.readDouble();
        timestamp = in.readLong();
        allItems = new ArrayList<>();
        in.readTypedList(allItems, BillItem.CREATOR);
    }

    public static final Creator<Bill> CREATOR = new Creator<Bill>() {
        @Override
        public Bill createFromParcel(Parcel in) {
            return new Bill(in);
        }

        @Override
        public Bill[] newArray(int size) {
            return new Bill[size];
        }
    };

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public ArrayList<BillItem> getAllItems() {
        return allItems;
    }

    public void setAllItems(ArrayList<BillItem> allItems) {
        this.allItems = allItems;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Double Long) {
        this.timestamp = timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.billId);
        parcel.writeDouble(this.totalPrice);
        parcel.writeByte((byte) (this.approved ? 1 : 0));
        parcel.writeDouble(this.tax);
        parcel.writeLong(this.timestamp);
        parcel.writeTypedList(this.allItems);
    }

    @Override
    public String toString() {
        return "Bill{" +
                "billId='" + billId + '\'' +
                ", totalPrice=" + totalPrice +
                ", approved=" + approved +
                ", tax=" + tax +
                ", timestamp=" + timestamp +
                ", allItems=" + allItems +
                '}';
    }
}
