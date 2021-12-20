package com.example.bills.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.bills.R;
import com.example.bills.adapters.BillsCustomAdapter;
import com.example.bills.adapters.GroupCustomAdapter;
import com.example.bills.misc.Miscellaneous;
import com.example.bills.models.Bill;
import com.example.bills.models.BillItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AllBillsActivity extends AppCompatActivity implements GroupCustomAdapter.OnGroupListener {

    private String groupId;
    private ArrayList<String> billIds;
    private ArrayList<Bill> allBills = new ArrayList<>();
    private ArrayList<String> allMembers = new ArrayList<>();
    private Bill currBill;
    Miscellaneous misc = new Miscellaneous();
    private RecyclerView recyclerView;
    private BillsCustomAdapter billsCustomAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_all_bills);
        groupId = getIntent().getExtras().getString("currGroupId");
        billIds = getIntent().getExtras().getStringArrayList("allBillIds");
        allMembers = getIntent().getExtras().getStringArrayList("allGroupMembers");
        Log.d("All Members", allMembers.toString());
        recyclerView = findViewById(R.id.all_bills_recyclerview);
        billsCustomAdapter = new BillsCustomAdapter(this, this.allBills, this );
        recyclerView.setAdapter(billsCustomAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        getAllBills();
    }

    @Override
    public void onGroupClick(int position) {
        currBill = this.allBills.get(position);
        Intent intent = new Intent(this, CurrBillActivity.class);
        Bundle bundle = new Bundle();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Group")
                .child(groupId).child("Bills").child(currBill.getBillId());
        databaseReference.child("allItems").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                try {
                    ArrayList<BillItem> tempBillItems = new ArrayList<>();
                    JSONArray billItemsJSON = new JSONArray(String.valueOf(task.getResult().getValue()));
                    //loop through the items and bundle them up for next intent
                    for (int i = 0; i < billItemsJSON.length(); i++) {
                        JSONObject currBillItemJSON = new JSONObject(String.valueOf(billItemsJSON.get(i)));
                        BillItem newBillItem = new BillItem(String.valueOf(currBillItemJSON.get("billItemId")),
                                misc.replacePercentage(String.valueOf(currBillItemJSON.get("itemName"))),
                                new Double(String.valueOf(currBillItemJSON.get("itemPrice"))),
                                new Integer(String.valueOf(currBillItemJSON.get("itemQuantity"))));
                        JSONArray usersList;
                        try {
                            usersList = new JSONArray(currBillItemJSON.getJSONArray("users").toString());
                            if (usersList.length() > 0) {
                                ArrayList<String> allUsersList = new ArrayList<>();
                                for (int j = 0; j < usersList.length(); j++) {
                                    allUsersList.add(usersList.get(j).toString());
                                }
                                newBillItem.setUsers(allUsersList);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        tempBillItems.add(newBillItem);
                    }
                    currBill.setAllItems(tempBillItems);
                    bundle.putParcelable("currActiveBill", currBill);
                    bundle.putStringArrayList("currMembers", allMembers);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getAllBills() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Group")
                .child(groupId).child("Bills");
        for(int i = 0; i < billIds.size(); i++) {
            databaseReference.child(billIds.get(i)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    try {
                        JSONObject billJSON = new JSONObject(task.getResult().getValue().toString());
                        Bill newBill = new Bill(billJSON.getString("billId"),
                                billJSON.getDouble("totalPrice"), billJSON.getDouble("tax"),
                                billJSON.getBoolean("approved"), billJSON.getLong("timestamp"));
                        if(newBill.isApproved()) {
                            allBills.add(newBill);
                            billsCustomAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
