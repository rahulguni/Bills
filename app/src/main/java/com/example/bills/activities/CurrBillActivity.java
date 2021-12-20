package com.example.bills.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bills.R;
import com.example.bills.adapters.CurrBillCustomAdapter;
import com.example.bills.adapters.GroupCustomAdapter;
import com.example.bills.misc.Miscellaneous;
import com.example.bills.models.Bill;
import com.example.bills.models.BillItem;
import com.example.bills.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
    private Miscellaneous misc = new Miscellaneous();

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
                       tempUser.setMoney(userTotalExpenses(tempUser.getPhone()));
                       allUsers.add(tempUser);
                       adapter.notifyDataSetChanged();
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }
           });
       }
    }

    private void userAllItems(String user, int position) {
        String allItemsName = "";
        ArrayList<BillItem> currBillItems = currBill.getAllItems();

        for(BillItem billItem: currBillItems) {
            int quantityCount = 0;
            boolean isPresent = false;
            for(String number: billItem.getUsers()) {
                if(number.equals(user)) {
                    quantityCount++;
                    isPresent = true;
                }
            }
            if(isPresent) {
                allItemsName += billItem.getItemName() + " - " + quantityCount + "  $" + Math.round((billItem.getItemPrice()/billItem.getItemQuantity()) * quantityCount * 100)/ 100.0 + "\n";
            }
            if(billItem.getUsers().isEmpty()) {
                allItemsName += billItem.getItemName() + " (C) $" + Math.round((billItem.getItemPrice() / allUsersId.size()) * 100) / 100.0 + "\n";
            }
        }
        allItemsName += "Tax - $" + Math.round((currBill.getTax() / allUsersId.size()) * 100) / 100.0 + "\n";

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Here is a list of all items from the bill for " + allUsers.get(position).getfName() + ":");
        alert.setMessage(allItemsName);
        alert.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    private double userTotalExpenses(String user) {
        double totalExpense = 0.0;

        ArrayList<BillItem> currBillItems = currBill.getAllItems();

        for(BillItem item: currBillItems) {
            double currItemPrice = item.getItemPrice() / item.getItemQuantity();
            int quantity = 0;
            for(String userNumber: item.getUsers()) {
                if(userNumber.equals(user)) {
                    quantity++;
                }
            }
            totalExpense += currItemPrice * quantity;
            //check for items nobody selected to equally divide the cost
            if(item.getUsers().isEmpty()) {
                totalExpense += currItemPrice / allUsersId.size();
            }
        }

        //Add Tax
        totalExpense += currBill.getTax()/allUsersId.size();

        totalExpense = Math.round(totalExpense * 100) / 100.0;
        return totalExpense;
    }

    @Override
    public void onGroupClick(int position) {
        userAllItems(this.allUsers.get(position).getPhone(), position);
    }
}