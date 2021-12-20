package com.example.bills.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bills.R;
import com.example.bills.activities.CurrBillActivity;
import com.example.bills.models.User;

import java.util.ArrayList;

public class CurrBillCustomAdapter extends RecyclerView.Adapter<CurrBillCustomAdapter.RecyclerViewHolder>{
    Context parentContext;
    ArrayList<User> allUsers;
    private GroupCustomAdapter.OnGroupListener onGroupListener;

    public CurrBillCustomAdapter(Context parentContext, ArrayList<User> allUsers, GroupCustomAdapter.OnGroupListener onGroupListener) {
        this.parentContext = parentContext;
        this.allUsers = allUsers;
        this.onGroupListener = onGroupListener;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parentContext).inflate(R.layout.curr_bill_recyclerview_adapter, parent, false);
        CurrBillCustomAdapter.RecyclerViewHolder recyclerViewHolder = new CurrBillCustomAdapter.RecyclerViewHolder(view, onGroupListener);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.userName.setText(allUsers.get(holder.getAdapterPosition()).getfName() + " " + allUsers.get(holder.getAdapterPosition()).getlName());
        holder.totalPrice.setText("$" + allUsers.get(holder.getAdapterPosition()).getMoney());
    }

    @Override
    public int getItemCount() {
        return this.allUsers.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView userName, totalPrice;
        GroupCustomAdapter.OnGroupListener onGroupListener;

        public RecyclerViewHolder(@NonNull View itemView,  GroupCustomAdapter.OnGroupListener onGroupListener) {
            super(itemView);
            this.userName = itemView.findViewById(R.id.bill_user_id);
            this.totalPrice = itemView.findViewById(R.id.bill_user_total);
            this.onGroupListener = onGroupListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onGroupListener.onGroupClick(getAdapterPosition());
        }
    }
}
