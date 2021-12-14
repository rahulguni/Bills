package com.example.bills.ui.dashboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.bills.MainActivity;
import com.example.bills.R;
import com.example.bills.SignInActivity;
import com.example.bills.databinding.FragmentDashboardBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DatabaseReference userDatabase;
    private FirebaseAuth mAuth;
    TextView fullName, email, phone;
    Button allGroups, signOut;
    JSONObject userJSON = new JSONObject();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        userDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        checkProfile();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fullName = (TextView) getView().findViewById(R.id.profile_full_name);
        email = (TextView) getView().findViewById(R.id.profile_email);
        phone = (TextView) getView().findViewById(R.id.profile_phone);
        allGroups = (Button) getView().findViewById(R.id.view_groups_btn);
        signOut = (Button) getView().findViewById(R.id.sign_out_btn);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOutClicked();
            }
        });
    }

    private void checkProfile() {
        userDatabase.child("User").child(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    try {
                        userJSON = new JSONObject(String.valueOf(task.getResult().getValue()));
                        fullName.setText(userJSON.get("fName").toString() + " " + userJSON.get("lName").toString());
                        email.setText(userJSON.get("email").toString());
                        phone.setText(userJSON.get("phone").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    public void signOutClicked() {
        AlertDialog.Builder signOutAlert = new AlertDialog.Builder(getContext());
        signOutAlert.setMessage("Are you sure you want to sign out?");
        signOutAlert.setCancelable(true);

        signOutAlert.setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Sign the user out and redirect to sign in page
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                startActivity(intent);
            }
        });

        signOutAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = signOutAlert.create();
        alertDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}