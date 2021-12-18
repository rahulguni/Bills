package com.example.bills.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bills.adapters.GroupCustomAdapter;
import com.example.bills.misc.Miscellaneous;
import com.example.bills.R;
import com.example.bills.databinding.FragmentHomeBinding;
import com.example.bills.models.Bill;
import com.example.bills.models.Group;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements GroupCustomAdapter.OnGroupListener {

    private FragmentHomeBinding binding;
    private GroupCustomAdapter adapter;
    private RecyclerView recyclerView;
    private Boolean hideAddGroupBtn = false;
    private String currUserPhone;
    private Bill mostRecentBill;

    //check user active groups
    private ArrayList<Group> currGroups;
    TextView noGroupText;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private JSONObject user;
    private JSONArray groups = new JSONArray();
    private Miscellaneous misc = new Miscellaneous();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if(!hideAddGroupBtn) {
            MenuInflater menuInflater = getActivity().getMenuInflater();
            menuInflater.inflate(R.menu.action_bar_menu, menu);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noGroupText = (TextView) getView().findViewById(R.id.no_groups_text);
        recyclerView = (RecyclerView) getView().findViewById(R.id.id_home_group_adapter);
        currGroups = new ArrayList<>();
        adapter = new GroupCustomAdapter(getContext(), this.currGroups, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        checkProfile();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Handle click on add button
        if(item.getItemId() == R.id.add_group_btn) {
            addGroupClicked();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void checkProfile() {
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference();
        userDatabase.child("UserDirectory").child(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    String phone = String.valueOf(task.getResult().getValue());
                    currUserPhone = phone;
                    misc.activeUserNumber = phone;
                    checkGroups(phone);
                }
            }
        });
    }


    private void checkGroups(String phone) {

        //Check user's groups to load up all groups in home page
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currUser = mAuth.getCurrentUser();
        if(currUser != null) {
            DatabaseReference usersTable = FirebaseDatabase.getInstance().getReference();
            usersTable.child("User").child(phone).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                    else {
                        try {
                            user =  new JSONObject(String.valueOf(task.getResult().getValue()));
                            //Search for groups from users JSON object's Groups and add them to the arraylist
                            groups = new JSONArray(user.getJSONArray("Groups").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException f) {
                            f.printStackTrace();
                        }
                    }
                    if(groups.length() == 0) {
                        fixUI(false);
                    }
                    else {
                        fixUI(true);
                    }
                    getGroups();
                }
            });
        }
    }

    private void fixUI(boolean bool) {
        if(bool) {
            noGroupText.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        else {
            noGroupText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }
    }

    private void getGroups() {
        for(int i = 0; i < this.groups.length(); i++) {
            DatabaseReference groupsTable = FirebaseDatabase.getInstance().getReference();
            try {
                groupsTable.child("Group").child((String) this.groups.get(i)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        //Extract data, append to array and reload recycler view
                        try {
                            JSONObject newGroup = new JSONObject(String.valueOf(task.getResult().getValue()));
                            JSONArray participants = new JSONArray(newGroup.getJSONArray("Participants").toString());
                            Group currGroup = new Group(newGroup.get("groupId").toString(),
                                    newGroup.get("adminId").toString(),
                                    misc.replacePercentage(newGroup.get("groupName").toString()));
                            currGroup.setParticipants(participants);
                            currGroups.add(0, currGroup);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        finally {
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void addGroupClicked() {
        AlertDialog.Builder addGroupAlert = new AlertDialog.Builder(getContext());
        addGroupAlert.setTitle("Enter the name for your group:");
        addGroupAlert.setCancelable(true);

        //set up input
        final EditText groupName = new EditText(getContext());
        groupName.setInputType(InputType.TYPE_CLASS_TEXT);
        addGroupAlert.setView(groupName);

        addGroupAlert.setPositiveButton("Make Group", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                addGroup(groupName);
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

    private void addGroup(TextView groupName) {
        //Make a new group with currUser as admin
        DatabaseReference groupDatabase;
        groupDatabase = FirebaseDatabase.getInstance().getReference();

        //create groupId
        String groupId = groupDatabase.push().getKey();
        Group newGroup = new Group( groupId,
                mAuth.getCurrentUser().getUid(),
                misc.replaceWhiteSpace(groupName.getText().toString().trim()));
        groupDatabase.child("Group").child(groupId).setValue(newGroup).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getContext(), "Congratulations on making a new group! Start dividing bills now.",
                            Toast.LENGTH_SHORT).show();
                    misc.addCurrGroupToUser(currUserPhone, groupId);
                    misc.addCurrUserToGroup(groupId, currUserPhone);
                    //Add group to the recyclerview
                    Group newGroup = new Group(groupId, mAuth.getCurrentUser().getUid(), groupName.getText().toString());
                    newGroup.setParticipantsForInitial(currUserPhone);
                    currGroups.add(0, newGroup);
                    fixUI(true);
                    adapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(getContext(), "Cannot Make Group. Please try again.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onGroupClick(int position) {
        MyGroupFragment myGroupFragment = new MyGroupFragment();
        this.hideAddGroupBtn = true;

        //Find the latest Bill
        DatabaseReference billsDb = FirebaseDatabase.getInstance().getReference().child("Group").child(currGroups.get(position).getGroupId());
        billsDb.child("Bills").orderByKey().limitToLast(1).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    try {
                        JSONObject billsJSON = new JSONObject(String.valueOf(task.getResult().getValue()));
                        //Get current Bill id
                        String billId = billsJSON.keys().next();
                        JSONObject currBillJSON = new JSONObject(billsJSON.get(billId).toString());
                        mostRecentBill = new Bill(currBillJSON.get("billId").toString(),
                                new Double(currBillJSON.get("totalPrice").toString()),
                                new Double(currBillJSON.get("tax").toString()),
                                new Boolean(currBillJSON.get("approved").toString()), new ArrayList<>());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    finally {
                        //Make a bundle of current group data to next fragment
                        Bundle bundle = new Bundle();
                        bundle.putString("currGroup", currGroups.get(position).toString());
                        if(mostRecentBill != null && !mostRecentBill.isApproved()) {
                            bundle.putParcelable("currBill", mostRecentBill);
                        }
                        myGroupFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.home_fragment_id, myGroupFragment)
                                .commit();
                    }
                }
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}