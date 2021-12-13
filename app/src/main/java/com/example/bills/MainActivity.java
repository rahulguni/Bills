package com.example.bills;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bills.models.Group;
import com.example.bills.ui.dashboard.DashboardFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.bills.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        mAuth = FirebaseAuth.getInstance();
    }

    //check if user is currently signed in, else go to sign in/sign up view
    @Override
    protected void onStart() {
        super.onStart();
        try {
            FirebaseUser currUser = mAuth.getCurrentUser();
            Log.d("user", currUser.getEmail());
        }
        catch(NullPointerException e) {
            //No user signed in, bring popup menu to sign in the user
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void addGroupClicked(MenuItem item) {
        AlertDialog.Builder addGroupAlert = new AlertDialog.Builder(this);
        addGroupAlert.setMessage("Enter the name for your group:");
        addGroupAlert.setCancelable(true);

        //set up input
        final EditText groupName = new EditText(this);
        groupName.setInputType(InputType.TYPE_CLASS_TEXT);
        addGroupAlert.setView(groupName);

        addGroupAlert.setPositiveButton("Make Group", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Make a new group with currUser as admin
                DatabaseReference groupDatabase;
                groupDatabase = FirebaseDatabase.getInstance().getReference();

                //create groupId
                String groupId = groupDatabase.push().getKey();
                Group newGroup = new Group( groupId, mAuth.getCurrentUser().getUid(), groupName.getText().toString());
                groupDatabase.child("Group").child(groupId).setValue(newGroup).addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Congratulations on making a new group! Start dividing bills now.",
                                    Toast.LENGTH_SHORT).show();
                            addCurrGroupToUser(groupId);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Cannot Make Group. Please try again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

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

    //Add currGroup to user's info
    private void addCurrGroupToUser(String groupId) {
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference groupRef = userDatabase.child("User");
        ArrayList<String> currGroupId = new ArrayList<>();
        currGroupId.add(groupId);
        groupRef.child(mAuth.getCurrentUser().getUid()).child("Groups").setValue(currGroupId);
    }

    //Remove Later, for testing purpose only until sign out button is added.
    @Override
    protected void onStop() {
        super.onStop();
        mAuth.signOut();
    }
}