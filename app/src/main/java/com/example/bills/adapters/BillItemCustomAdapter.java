package com.example.bills.adapters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bills.R;
import com.example.bills.models.BillItem;

import java.util.ArrayList;

public class BillItemCustomAdapter extends RecyclerView.Adapter<BillItemCustomAdapter.RecyclerViewHolder> {
    Context parentContext;
    ArrayList<BillItem> allBillItems;
    private OnBillItemListener onBillItemListener;
    private int selected_position;

    public BillItemCustomAdapter(Context parentContext, ArrayList<BillItem> allBillItems, OnBillItemListener onBillItemListener) {
        this.parentContext = parentContext;
        this.allBillItems = allBillItems;
        this.onBillItemListener = onBillItemListener;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parentContext).inflate(R.layout.billitem_recyclerview_adapter, parent, false);
        BillItemCustomAdapter.RecyclerViewHolder recyclerViewHolder = new BillItemCustomAdapter.RecyclerViewHolder(view, onBillItemListener);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.itemName.setText(allBillItems.get(holder.getAdapterPosition()).getItemName());
        holder.itemPrice.setText(priceToString(allBillItems.get(holder.getAdapterPosition()).getItemPrice()));
        holder.itemQuantity.setText(String.valueOf(allBillItems.get(holder.getAdapterPosition()).getItemQuantity()));
        holder.itemView.setBackgroundColor(selected_position == position ? Color.LTGRAY : Color.TRANSPARENT);
    }

    @Override
    public int getItemCount() {
        return allBillItems.size();
    }

    private String priceToString(double price) {
        Double rounded = Math.round(price * 100)/100.0;
        return "$" + rounded;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView itemName, itemQuantity, itemPrice;
        OnBillItemListener onBillItemListener;

        public RecyclerViewHolder(@NonNull View itemView, OnBillItemListener onBillItemListener) {
            super(itemView);
            this.itemName = itemView.findViewById(R.id.itemName);
            this.itemQuantity = itemView.findViewById(R.id.itemQuantity);
            this.itemPrice = itemView.findViewById(R.id.itemPrice);
            this.onBillItemListener = onBillItemListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // Below line is just like a safety check, because sometimes holder could be null,
            // in that case, getAdapterPosition() will return RecyclerView.NO_POSITION
            if (getAdapterPosition() == RecyclerView.NO_POSITION) return;

            // Updating old as well as new positions
            notifyItemChanged(selected_position);
            selected_position = getAdapterPosition();
            notifyItemChanged(selected_position);
            onBillItemListener.onGroupClick(getAdapterPosition());
        }
    }

    public interface OnBillItemListener {
        void onGroupClick(int position);
    }

}
