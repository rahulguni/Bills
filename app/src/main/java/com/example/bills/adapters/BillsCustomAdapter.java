package com.example.bills.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bills.R;
import com.example.bills.models.Bill;
import com.example.bills.models.Group;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BillsCustomAdapter extends RecyclerView.Adapter<BillsCustomAdapter.RecyclerViewHolder> {
    Context parentContext;
    ArrayList<Bill> allBills;
    private GroupCustomAdapter.OnGroupListener mOnGroupListener;

    public BillsCustomAdapter(Context parentContext, ArrayList<Bill> allBills, GroupCustomAdapter.OnGroupListener onGroupListener) {
        this.parentContext = parentContext;
        this.allBills = allBills;
        this.mOnGroupListener = onGroupListener;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parentContext).inflate(R.layout.all_bills_recyclerview_adapter, parent, false);
        BillsCustomAdapter.RecyclerViewHolder recyclerViewHolder = new BillsCustomAdapter.RecyclerViewHolder(view, mOnGroupListener);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.billDate.setText(toDateString(this.allBills.get(holder.getAdapterPosition()).getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return this.allBills.size();
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView billDate;
        GroupCustomAdapter.OnGroupListener onGroupListener;

        public RecyclerViewHolder(@NonNull View itemView, GroupCustomAdapter.OnGroupListener onGroupListener) {
            super(itemView);
            this.billDate = itemView.findViewById(R.id.bill_date_id);
            this.onGroupListener = onGroupListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onGroupListener.onGroupClick(getAdapterPosition());
        }
    }

    private String toDateString(long timestamp) {
        String dateString = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date(timestamp * 1000);
        dateString = sdf.format(date);

        return dateString;
    }
}
