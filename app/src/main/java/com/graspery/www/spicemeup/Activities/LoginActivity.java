package com.graspery.www.spicemeup.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.graspery.www.spicemeup.Firebase.FirebaseDatabaseHelper;
import com.graspery.www.spicemeup.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseDatabaseHelper mFirebaseDatabaseHelper;
    private EditText emailEditText;
    private EditText passwordEditText;
    private AppCompatButton loginButton;
    private TextView registerTextView;
    private TextView guestContinue;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private boolean isRegisteredUser;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        isRegisteredUser = false;

        initializeComponents();
    }

    private void initializeComponents() {
        mFirebaseDatabaseHelper = new FirebaseDatabaseHelper(this);

        mProgressBar = findViewById(R.id.login_progress_bar);
        emailEditText = findViewById(R.id.input_email);
        passwordEditText = findViewById(R.id.input_password);
        loginButton = findViewById(R.id.btn_login);
        registerTextView = findViewById(R.id.link_signup);

        guestContinue = findViewById(R.id.guest_continue);
        guestContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(LoginActivity.this, NetflixActivity.class));
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()) {
                    signInExistingUser(emailEditText.getText().toString(), passwordEditText.getText().toString());
                }
            }
        });

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterUserActivity.class));
                finish();
            }
        });
    }

    public void signInExistingUser(String email, String password) {
        mProgressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            currentUser = mAuth.getCurrentUser();
                            isRegisteredUser = true;
                            Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            isRegisteredUser = false;
                            Toast.makeText(LoginActivity.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public boolean validate() {
        boolean valid = true;

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("enter a valid email address");
            valid = false;
        } else {
            emailEditText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordEditText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordEditText.setError(null);
        }

        return valid;
    }
}
