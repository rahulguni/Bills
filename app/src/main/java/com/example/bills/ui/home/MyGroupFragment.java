package com.example.bills.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bills.MainActivity;
import com.example.bills.Miscellaneous;
import com.example.bills.R;
import com.example.bills.databinding.FragmentHomeBinding;
import com.example.bills.models.Group;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class MyGroupFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Group currGroup;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currGroup = getDataFromBundle(this.getArguments());
        setHasOptionsMenu(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(currGroup.getGroupName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_group, container, false);
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void checkMemberToGroup() {
        AlertDialog.Builder addParticipantAlert = new AlertDialog.Builder(getContext());
        addParticipantAlert.setMessage("Enter the phone number to add:");
        addParticipantAlert.setCancelable(true);

        //set up input
        final EditText groupName = new EditText(getContext());
        groupName.setInputType(InputType.TYPE_CLASS_TEXT);
        addParticipantAlert.setView(groupName);

        addParticipantAlert.setPositiveButton("Add Participants", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                //Search if the phone number is in users table.
                //Add to groupMembers if true, else make toast and alert the user
                DatabaseReference db = FirebaseDatabase.getInstance().getReference();

                db.child("User").child(groupName.getText().toString()).get().addOnCompleteListener(getActivity(), new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()) {
                            try {
                                JSONObject currGroupMember = new JSONObject(String.valueOf(task.getResult().getValue()));
                                Toast.makeText(getContext(), "Person Found. LESGOOO",
                                        Toast.LENGTH_SHORT).show();

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
        });

        addParticipantAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = addParticipantAlert.create();
        alertDialog.show();
    }

    private void viewAllMembers() {

    }

    private Group getDataFromBundle(Bundle bundle) {
        Group newGroup = new Group();

        String groupData = bundle.getString("currGroup");

        try {

            JSONObject groupJSON = new JSONObject(String.valueOf(groupData));
            newGroup = new Group(groupJSON.get("groupId").toString(),
                    groupJSON.get("adminId").toString(),
                    new Miscellaneous().replacePercentage(groupJSON.get("groupName").toString()));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("Group", groupData);
        return newGroup;
    }

}