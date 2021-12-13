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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    TextView email, password;
    Button signIn, signUp;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.email_id);
        password = findViewById(R.id.password_id);
        signIn = findViewById(R.id.signIn_btn);
        signUp = findViewById(R.id.signup_btn);
        mAuth = FirebaseAuth.getInstance();
    }

    public void signInClicked(View v) {
        String currEmail = email.getText().toString().trim();
        String currPassword = password.getText().toString().trim();
        Log.d("password", currPassword);
        if(new Miscellaneous().checkTextFields(new String[]{currEmail, currPassword})) {
            mAuth.signInWithEmailAndPassword(currEmail, currPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Login_Success", "signInWithEmail:success");
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("LogIn_Fail", "signInWithEmail:failure", task.getException());
                                Toast.makeText(SignInActivity.this, "Authentication failed. Check email and password.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else {
            Toast.makeText(this, "Please make sure all fields are filled and try again", Toast.LENGTH_SHORT).show();
        }
    }

    //change activity to signup when sign up button clicked
    public void signUpClicked(View v) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }


}