package com.example.bills;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bills.models.Group;

import java.util.ArrayList;

public class RequestsCustomAdapter extends RecyclerView.Adapter<RequestsCustomAdapter.RecyclerViewHolder>{
    Context parentContext;
    ArrayList<Group> allRequests;

    public RequestsCustomAdapter(Context parentContext, ArrayList<Group> allRequests) {
        this.parentContext = parentContext;
        this.allRequests = allRequests;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parentContext).inflate(R.layout.requests_recyclerview_adapter, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.groupName.setText(allRequests.get(holder.getAdapterPosition()).getGroupName());
        holder.sender.setText("From: " + allRequests.get(holder.getAdapterPosition()).getParticipants().get(0));
        holder.peopleNumber.setText("Group Members: " + String.valueOf(allRequests.get(holder.getAdapterPosition()).getParticipants().size()));
    }

    @Override
    public int getItemCount() {
        return this.allRequests.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView groupName, sender, peopleNumber;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.groupName = itemView.findViewById(R.id.requests_group_name);
            this.sender = itemView.findViewById(R.id.requests_sender);
            this.peopleNumber = itemView.findViewById(R.id.people_number_in_group);
        }

    }

    private void confirmRequest(){

    }

}
