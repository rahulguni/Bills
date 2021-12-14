package com.example.bills;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bills.models.Group;

import java.util.ArrayList;

public class GroupCustomAdapter extends RecyclerView.Adapter<GroupCustomAdapter.RecyclerViewHolder> {
    Context parentContext;
    ArrayList<Group> allGroups;
    private OnGroupListener mOnGroupListener;

    public GroupCustomAdapter(Context parentContext, ArrayList<Group> allGroups, OnGroupListener onGroupListener) {
        this.parentContext = parentContext;
        this.allGroups = allGroups;
        this.mOnGroupListener = onGroupListener;
    }

    @NonNull
    @Override
    public GroupCustomAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parentContext).inflate(R.layout.home_group_recyclerview_adapter, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view, mOnGroupListener);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupCustomAdapter.RecyclerViewHolder holder, int position) {
        holder.groupName.setText(allGroups.get(holder.getAdapterPosition()).getGroupName());
    }

    @Override
    public int getItemCount() {
        return this.allGroups.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView groupName;
        OnGroupListener onGroupListener;

        public RecyclerViewHolder(@NonNull View itemView, OnGroupListener onGroupListener) {
            super(itemView);
            this.groupName = itemView.findViewById(R.id.home_group_name);
            this.onGroupListener = onGroupListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onGroupListener.onGroupClick(getAdapterPosition());
        }
    }

    public interface OnGroupListener {
        void onGroupClick(int position);
    }
}
