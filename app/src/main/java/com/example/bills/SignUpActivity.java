package com.example.bills;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bills.models.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;

public class SignUpActivity extends AppCompatActivity {

    TextView firstName, lastName, email, password, phone;
    Button signUp;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phone_number);
        signUp = findViewById(R.id.sign_up);
        mAuth = FirebaseAuth.getInstance();
    }

    public void signUpBtnClicked(View v) {
        String currEmail = email.getText().toString().trim();
        String currPassword = password.getText().toString().trim();
        String currFirstName = firstName.getText().toString().trim();
        String currLastName = lastName.getText().toString().trim();
        String currPhone = phone.getText().toString().trim();

        if(new Miscellaneous().checkTextFields(new String[]{currEmail, currPassword, currPhone, currFirstName, currLastName})) {
            Log.d("Email", currEmail);
            mAuth.createUserWithEmailAndPassword(currEmail, currPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("SignUp_Success", "createUserWithEmail:success");

                                //Save the user in Users table
                                FirebaseUser user = mAuth.getCurrentUser();
                                User curr = new User(user.getUid(), currFirstName, currLastName, currEmail, currPhone);
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference userTable = database.getReference("User");
                                userTable.child(curr.getPhone()).setValue(curr).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else {
                                            //Delete current user to restart authentication
                                            mAuth.getCurrentUser().delete();
                                            Toast.makeText(SignUpActivity.this, "Cannot Sign Up. Please try again.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("SignUp_Error", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignUpActivity.this, "Cannot Sign Up. Please try again.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else {
            Toast.makeText(this, "Please make sure all fields are filled and try again", Toast.LENGTH_SHORT).show();
        }
    }



}