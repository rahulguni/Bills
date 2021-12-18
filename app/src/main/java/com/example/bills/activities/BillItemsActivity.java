package com.example.bills.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bills.R;
import com.example.bills.adapters.BillItemCustomAdapter;
import com.example.bills.misc.Miscellaneous;
import com.example.bills.models.Bill;
import com.example.bills.models.BillItem;
import com.example.bills.models.Group;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class BillItemsActivity extends AppCompatActivity {

    private Group currGroup;
    private BillItemCustomAdapter adapter;
    private RecyclerView recyclerView;
    private Bill currBill;
    private ArrayList<BillItem> allItems = new ArrayList<>();
    TextView totalPriceText, taxPriceText, noItemsText;
    Button confirmBill;
    Miscellaneous misc = new Miscellaneous();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_items);

        currGroup = misc.getGroupDataFromBundle(getIntent().getExtras());
        totalPriceText = findViewById(R.id.totalBill);
        taxPriceText = findViewById(R.id.totalTax);
        noItemsText = findViewById(R.id.no_items_bill_text);
        confirmBill = findViewById(R.id.confirm_bill_upload);
        adapter = new BillItemCustomAdapter(this, this.allItems);
        recyclerView = findViewById(R.id.initiali_bills_recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        fixUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Handle Click on Add Button
        if(item.getItemId() == R.id.add_group_btn) {
            addBillItem();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Save current Bill in database
    public void saveBill(View v) {
        //Present tax alert and save bill
        AlertDialog.Builder addGroupAlert = new AlertDialog.Builder(this);
        addGroupAlert.setTitle("Check your total tax:");
        addGroupAlert.setCancelable(true);

        //set up input
        final EditText taxAmount = new EditText(this);
        taxAmount.setInputType(InputType.TYPE_CLASS_TEXT);
        addGroupAlert.setView(taxAmount);

        addGroupAlert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                saveCurrBill(new Double(taxAmount.getText().toString().trim()));
            }
        });

        addGroupAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = addGroupAlert.create();
        alertDialog.show();
    }

    private void saveCurrBill(Double tax) {
        DatabaseReference groupsDb = FirebaseDatabase.getInstance().getReference().child("Group").child(currGroup.getGroupId()).child("Bills");
        String groupId = groupsDb.push().getKey();
        fixItemTitlesForJSON();
        Bill newBill = new Bill(groupId, getTotal(), tax, false, allItems);
        groupsDb.child(groupId).setValue(newBill);
        allItems.clear();
        adapter.notifyDataSetChanged();
        checkTotal();
        fixUI();
    }

    //Present alert to add BillItem to arraylist
    public void addBillItem() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.add_billitem_dialog);

        //Initializing the views of the dialog
        final EditText itemName = dialog.findViewById(R.id.item_name_dialog);
        final EditText itemQuantity = dialog.findViewById(R.id.item_quantity_dialog);
        final EditText itemPrice = dialog.findViewById(R.id.item_price_dialog);
        Button addItemButton = dialog.findViewById(R.id.add_bill_item_dialog);

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BillItem newItem = new BillItem(FirebaseDatabase.getInstance().getReference().push().getKey(),
                        itemName.getText().toString().trim(), new Double(itemPrice.getText().toString().trim()),
                        new Integer(itemQuantity.getText().toString().trim()));
                allItems.add(newItem);
                adapter.notifyDataSetChanged();
                checkTotal();
                fixUI();
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void checkTotal() {
        double total = 0;
        for(BillItem billItem: allItems) {
            total += billItem.getItemPrice();
        }
        String totalString = "Total: $" + String.valueOf(total);
        totalPriceText.setText(totalString);
    }

    private double getTotal() {
        double total = 0.0;
        for(BillItem billItem: allItems) {
            total += billItem.getItemPrice();
        }
        return total;
    }

    private void fixUI() {
        if(allItems.size() == 0) {
            noItemsText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }
        else {
            noItemsText.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void fixItemTitlesForJSON() {
        for(int i = 0; i < allItems.size(); i++) {
            allItems.get(i).setItemName(misc.replaceWhiteSpace(allItems.get(i).getItemName()));
        }
    }
}