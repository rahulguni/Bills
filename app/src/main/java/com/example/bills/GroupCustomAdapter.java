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

    public GroupCustomAdapter(Context parentContext, ArrayList<Group> allGroups) {
        this.parentContext = parentContext;
        this.allGroups = allGroups;
    }

    @NonNull
    @Override
    public GroupCustomAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parentContext).inflate(R.layout.home_group_recyclerview_adapter, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
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

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView groupName;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.home_group_name);
        }
    }
}
