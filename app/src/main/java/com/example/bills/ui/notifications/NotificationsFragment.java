package com.example.bills.ui.notifications;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bills.adapters.GroupCustomAdapter;
import com.example.bills.misc.Miscellaneous;
import com.example.bills.R;
import com.example.bills.adapters.RequestsCustomAdapter;
import com.example.bills.databinding.FragmentNotificationsBinding;
import com.example.bills.models.Group;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment implements GroupCustomAdapter.OnGroupListener {

    private FragmentNotificationsBinding binding;
    private RequestsCustomAdapter adapter;
    private RecyclerView recyclerView;
    private String currUserPhone;

    //To check user requests from Groups
    private ArrayList<Group> allRequests;
    TextView noRequestsText;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private JSONObject user;
    private JSONArray requests = new JSONArray();
    private Miscellaneous misc = new Miscellaneous();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noRequestsText = (TextView) getView().findViewById(R.id.no_requests_text);
        recyclerView = (RecyclerView) getView().findViewById(R.id.id_requests_recyclerView);
        allRequests = new ArrayList<>();
        adapter = new RequestsCustomAdapter(getContext(), this.allRequests, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        checkProfile();
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
                    checkRequests(phone);
                }
            }
        });
    }

    private void checkRequests(String phone) {
        //Check user's requests to load all requests
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
                            requests = new JSONArray(user.getJSONArray("Requests").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException f) {
                            f.printStackTrace();
                        }
                    }
                    if(requests.length() == 0) {
                        fixUI(false);
                    }
                    else {
                        fixUI(true);
                    }
                    getRequests();
                }
            });
        }
    }

    private void getRequests() {
        for(int i = 0; i < this.requests.length(); i++) {
            DatabaseReference groupsTable = FirebaseDatabase.getInstance().getReference();
            try {
                groupsTable.child("Group").child((String) this.requests.get(i)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                            allRequests.add(0, currGroup);
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

    private void fixUI(boolean bool) {
        if(bool) {
            noRequestsText.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        else {
            noRequestsText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onGroupClick(int position) {
        AlertDialog.Builder requestAlert = new AlertDialog.Builder(getContext());
        requestAlert.setTitle("Do you want to join " + allRequests.get(position).getGroupName() + "?");
        requestAlert.setCancelable(true);

        requestAlert.setPositiveButton("Join", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                acceptRequest(position);
            }
        });

        requestAlert.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteRequest(position);
            }
        });

        AlertDialog alertDialog = requestAlert.create();
        alertDialog.show();
    }

    private void acceptRequest(int position) {
        //Add current user to group and delete request
        misc.addCurrUserToGroup(allRequests.get(position).getGroupId(), currUserPhone);
        misc.addCurrGroupToUser(currUserPhone, allRequests.get(position).getGroupId());
        deleteRequest(position);
    }

    private void deleteRequest(int position) {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("User").child(currUserPhone).child("Requests");
        ArrayList<String> currRequests = new ArrayList<>();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()) {
                    String mGroupId = ds.getValue(String.class);
                    currRequests.add(mGroupId);
                }
                //Remove current request from the array
                for(int i = 0; i < currRequests.size(); i++) {
                    if(allRequests.get(position).getGroupId().equals(currRequests.get(i))) {
                        allRequests.remove(position);
                        currRequests.remove(i);
                    }
                }
                userReference.setValue(currRequests);
                adapter.notifyDataSetChanged();
                if(allRequests.size() == 0) {
                    noRequestsText.setVisibility(View.VISIBLE);
                }
                else {
                    noRequestsText.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error", error.getMessage());
            }
        };
        userReference.addListenerForSingleValueEvent(valueEventListener);
    }
}