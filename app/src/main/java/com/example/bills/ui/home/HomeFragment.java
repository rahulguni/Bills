package com.example.bills.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bills.GroupCustomAdapter;
import com.example.bills.MainActivity;
import com.example.bills.Miscellaneous;
import com.example.bills.R;
import com.example.bills.databinding.FragmentHomeBinding;
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

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    GroupCustomAdapter adapter;
    RecyclerView recyclerView;

    //check user active groups
    ArrayList<Group> currGroups;
    TextView noGroupText;
    FirebaseAuth mAuth;
    JSONObject user;
    JSONArray groups = new JSONArray();

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
        adapter = new GroupCustomAdapter(getContext(), this.currGroups);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        checkGroups();
    }

    private void checkGroups() {

        //Check user's groups to load up all groups in home page
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currUser = mAuth.getCurrentUser();
        if(currUser != null) {
            DatabaseReference usersTable = FirebaseDatabase.getInstance().getReference();
            usersTable.child("User").child(currUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                        noGroupText.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                    }
                    else {
                        noGroupText.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    getGroups();
                }
            });
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
                            currGroups.add(new Group(newGroup.get("groupId").toString(),
                                    newGroup.get("adminId").toString(),
                                    new Miscellaneous().replacePercentage(newGroup.get("groupName").toString())));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}