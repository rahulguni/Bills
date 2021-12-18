package com.example.bills.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bills.activities.BillItemsActivity;
import com.example.bills.misc.Miscellaneous;
import com.example.bills.R;
import com.example.bills.databinding.FragmentHomeBinding;
import com.example.bills.models.Bill;
import com.example.bills.models.Group;
import com.example.bills.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyGroupFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Bill currBill;
    private Group currGroup;
    private ArrayList<User> allUsers = new ArrayList<>();
    Button confirmBill;
    Miscellaneous misc = new Miscellaneous();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currGroup = misc.getGroupDataFromBundle(this.getArguments());
        getUserNames();
        setHasOptionsMenu(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(currGroup.getGroupName());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_my_group, container, false);
        fragmentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        confirmBill = getActivity().findViewById(R.id.confirm_bill_upload);
        confirmBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmUploadClicked();
            }
        });

        checkIfBillPending();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Handle click on add button
        if(item.getItemId() == R.id.add_member) {
            checkMemberToGroup();
            return true;
        }
        else if(item.getItemId() == R.id.view_member) {
            viewAllMembers();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.action_bar_mygroup_menu, menu);

    }

    private void checkMemberToGroup() {
        AlertDialog.Builder addParticipantAlert = new AlertDialog.Builder(getContext());
        addParticipantAlert.setTitle("Enter the phone number to add:");
        addParticipantAlert.setCancelable(true);

        //set up input
        final EditText memberNumber = new EditText(getContext());
        memberNumber.setInputType(InputType.TYPE_CLASS_PHONE);
        addParticipantAlert.setView(memberNumber);

        addParticipantAlert.setPositiveButton("Add Participants", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                addMembers(memberNumber);
            }
        });

        addParticipantAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = addParticipantAlert.create();
        alertDialog.show();
    }

    private void addMembers(TextView memberNumber) {
        if(!checkIfAlreadyMember(memberNumber.getText().toString())) {
            //Search if the phone number is in users table.
            //Add to groupMembers if true, else make toast and alert the user
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();

            db.child("User").child(memberNumber.getText().toString()).get().addOnCompleteListener(getActivity(), new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()) {
                        try {
                            JSONObject currGroupMember = new JSONObject(String.valueOf(task.getResult().getValue()));
                            //Send request to person
                            sendRequest(currGroupMember.get("phone").toString());

                        } catch (JSONException e) {
                            Toast.makeText(getContext(), "No person found. Make sure you have the correct number.",
                                    Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                    else {
                        Toast.makeText(getContext(), "Cannot connect at this time. Please try again",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            Toast.makeText(getContext(), "Member is already in your group!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void sendRequest(String user) {
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference groupRef = userDatabase.child("User").child(user).child("Requests");
        ArrayList<String> currRequests = new ArrayList<>();

        //Check for other Participants and append to the list
        ValueEventListener valueEventListener = new ValueEventListener() {
            boolean alreadySent = false;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()) {
                    String mCurrGroup = ds.getValue(String.class);
                    if(mCurrGroup.equals(currGroup.getGroupId())) {
                        alreadySent = true;
                    }
                    currRequests.add(mCurrGroup);
                }
                if(alreadySent) {
                    Toast.makeText(getContext(), ("Request to " + user + " is already sent!" ),
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    currRequests.add(currGroup.getGroupId());
                    groupRef.setValue(currRequests);
                    Toast.makeText(getContext(), ("Request sent to " + user ),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error", error.getMessage());
            }
        };

        groupRef.addListenerForSingleValueEvent(valueEventListener);
    }

    private void viewAllMembers() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("All Members of " + currGroup.getGroupName() + ":");
        String members = "";
        for(int i = 0 ; i < allUsers.size(); i++) {
            if(i == 0) {
                members = allUsers.get(i).getfName() + " " + allUsers.get(i).getlName() + "  (Admin)\n";
            }
            else {
                members += allUsers.get(i).getfName() + " " + allUsers.get(i).getlName() + "\n";
            }
        }
        alert.setMessage(members);
        alert.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    private void getUserNames() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("User");
        for(int i = 0; i < currGroup.getParticipants().size(); i++) {
            usersRef.child(currGroup.getParticipants().get(i)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    try {
                        JSONObject thisUser = new JSONObject(String.valueOf(task.getResult().getValue()));
                        User newUser = new User(thisUser.get("userId").toString(),
                                thisUser.get("fName").toString(),
                                thisUser.get("lName").toString(), thisUser.get("email").toString(),
                                thisUser.get("phone").toString());
                        allUsers.add(newUser);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    private boolean checkIfAlreadyMember(String number) {
        for(int i=0; i < currGroup.getParticipants().size(); i++) {
            if(number.equals(currGroup.getParticipants().get(i))) {
                return true;
            }
        }
        return false;
    }

    //If there is a
    private void confirmUploadClicked() {
        Intent intent = new Intent(getContext(), BillItemsActivity.class);
        intent.putExtras(this.getArguments());
        startActivity(intent);
    }

    //Check if a bill is pending, check approved of latest bill
    //If pending, take intent directly to next fragment
    private void checkIfBillPending() {
        try{
            currBill = getArguments().getParcelable("currBill");
        }
        catch(NullPointerException e) {
            e.printStackTrace();
        }
        finally {
            if(currBill != null) {
                //Get all bill items and go to choose bill intent.
                Log.d("CurrBill", currBill.getBillId());
            }
        }
    }

}