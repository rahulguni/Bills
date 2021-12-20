package com.example.bills.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bills.R;
import com.example.bills.adapters.CurrBillCustomAdapter;
import com.example.bills.adapters.GroupCustomAdapter;
import com.example.bills.models.Bill;
import com.example.bills.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CurrBillActivity extends AppCompatActivity implements GroupCustomAdapter.OnGroupListener {

    private RecyclerView recyclerView;
    private CurrBillCustomAdapter adapter;
    private Bill currBill = new Bill();
    private ArrayList<User> allUsers = new ArrayList<>();
    private ArrayList<String> allUsersId = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allUsersId = getIntent().getExtras().getStringArrayList("currMembers");
        currBill = getIntent().getExtras().getParcelable("currActiveBill");
        Log.d("TAG", currBill.toString());
        setContentView(R.layout.activity_curr_bill);
        recyclerView = findViewById(R.id.all_users_recyclerview);
        adapter = new CurrBillCustomAdapter(this, this.allUsers, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        getAllUsers();
    }

    private void getAllUsers() {
       DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
       for(int i = 0; i < allUsersId.size(); i++) {
           databaseReference.child(allUsersId.get(i)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
               @Override
               public void onComplete(@NonNull Task<DataSnapshot> task) {
                   try {
                       JSONObject tempUserJSON = new JSONObject(task.getResult().getValue().toString());
                       User tempUser = new User(tempUserJSON.getString("userId"), tempUserJSON.getString("fName"),
                               tempUserJSON.getString("lName"), tempUserJSON.getString("email"), tempUserJSON.getString("phone"));
                       allUsers.add(tempUser);
                       adapter.notifyDataSetChanged();
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }
           });
       }
    }

    @Override
    public void onGroupClick(int position) {

    }
}