package com.example.bills.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bills.R;
import com.example.bills.models.BillItem;

import java.util.ArrayList;

public class BillItemCustomAdapter extends RecyclerView.Adapter<BillItemCustomAdapter.RecyclerViewHolder> {
    Context parentContext;
    ArrayList<BillItem> allBillItems;

    public BillItemCustomAdapter(Context parentContext, ArrayList<BillItem> allBillItems) {
        this.parentContext = parentContext;
        this.allBillItems = allBillItems;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parentContext).inflate(R.layout.billitem_recyclerview_adapter, parent, false);
        BillItemCustomAdapter.RecyclerViewHolder recyclerViewHolder = new BillItemCustomAdapter.RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.itemName.setText(allBillItems.get(holder.getAdapterPosition()).getItemName());
        holder.itemPrice.setText(priceToString(allBillItems.get(holder.getAdapterPosition()).getItemPrice()));
        holder.itemQuantity.setText(String.valueOf(allBillItems.get(holder.getAdapterPosition()).getItemQuantity()));
    }

    @Override
    public int getItemCount() {
        return allBillItems.size();
    }

    private String priceToString(double price) {
        Double rounded = Math.round(price * 100)/100.0;
        return "$" + String.valueOf(rounded);
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemQuantity, itemPrice;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemName = itemView.findViewById(R.id.itemName);
            this.itemQuantity = itemView.findViewById(R.id.itemQuantity);
            this.itemPrice = itemView.findViewById(R.id.itemPrice);
        }
    }
}
