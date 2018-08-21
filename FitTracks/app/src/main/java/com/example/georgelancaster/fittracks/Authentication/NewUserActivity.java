package com.example.georgelancaster.fittracks.Authentication;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.georgelancaster.fittracks.Hub.HubActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.example.georgelancaster.fittracks.R;

import static android.content.ContentValues.TAG;

public class NewUserActivity extends Activity {
    private EditText username;
    private EditText password;
    private EditText confirmPassword;
    private ProgressBar spinner;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        mAuth = FirebaseAuth.getInstance();
        String name = getIntent().getStringExtra("USERNAME");
        username = (EditText) findViewById(R.id.Username);
        password = (EditText) findViewById(R.id.Password);
        spinner = (ProgressBar) findViewById(R.id.progressBarNewUser);
        confirmPassword = (EditText) findViewById(R.id.ConfirmPassword);
        username.setText(name);
    }

    /**
     * Revert back to the login screen
     *
     * @param view the button
     */
    public void backToLogin(View view) {
        Intent intent = new Intent(NewUserActivity.this, LoginActivity.class);
        NewUserActivity.this.startActivity(intent);
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    /**
     * Create a new user
     *
     * @param view the button
     */
    public void createNewUser(View view) {
        String theUsername = username.getText().toString().trim();
        String thePassword = password.getText().toString().trim();
        String thePasswordConf = confirmPassword.getText().toString().trim();

        if (!isEmailValid(theUsername)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (theUsername.isEmpty()) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (thePassword.length() < 6) {
            Toast.makeText(this, "Password must be longer than 6 chars", Toast.LENGTH_SHORT).show();
            return;
        }
        if (thePassword.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!thePassword.equals(thePasswordConf)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        spinner.setVisibility(View.VISIBLE);
        final String finUsername = theUsername;
        final String finPassword = thePassword;
        mAuth.createUserWithEmailAndPassword(theUsername, thePassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), finUsername, Toast.LENGTH_SHORT).show();
                            signIn(finUsername, finPassword);
                        } else {
                            Toast.makeText(getApplicationContext(), "Account name in use", Toast.LENGTH_SHORT).show();
                            spinner.setVisibility(View.INVISIBLE);
                            Log.i("Response", "Failed to create user:" + task.getException().getMessage());
                        }
                    }
                });
    }

    /**
     * Sign in with username and password
     *
     * @param emailAddress the username to login
     * @param password     the password
     */
    private void signIn(String emailAddress, String password) {
        mAuth.signOut();
        mAuth.signInWithEmailAndPassword(emailAddress, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(NewUserActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                            appLogin();
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(NewUserActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            spinner.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    /**
     * Login to the application
     */
    private void appLogin() {
        spinner.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(NewUserActivity.this, HubActivity.class);
        NewUserActivity.this.startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    /**
     * So that Login Activity can have no history after login.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(NewUserActivity.this, LoginActivity.class);
        NewUserActivity.this.startActivity(intent);
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    /**
     * checks if a string is an email address
     *
     * @param email string to check if valid
     * @return true if email is valid
     */
    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
