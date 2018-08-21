package com.example.georgelancaster.fittracks.Authentication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.georgelancaster.fittracks.Hub.HubActivity;
import com.example.georgelancaster.fittracks.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import static android.content.ContentValues.TAG;

/**
 * The first screen of the application. Logs a user into the application.
 */
public class LoginActivity extends Activity {

    private FirebaseAuth mAuth;
    private EditText username;
    private EditText password;
    private GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 9001;
    private ProgressBar spinner;
    private SignInButton googleSigninButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        username = (EditText) findViewById(R.id.Username);
        password = (EditText) findViewById(R.id.Password);
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("262554469631-t6opq02i3s7ushh1c6h12mi7n0fkhcfo.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSigninButton = (SignInButton) findViewById(R.id.googleButton);
        googleSigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn(view);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.v("notnull", "currentUser");
        }
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            Log.v("notnull", "googUser");
        }
    }

    /**
     * Login with username and password
     *
     * @param view the Button
     */
    public void login(View view) {
        spinner.setVisibility(View.VISIBLE);
        String theUsername = username.getText().toString().toLowerCase();
        String thePassword = password.getText().toString();
        if (!isEmailValid(theUsername)) {
            Toast.makeText(LoginActivity.this,
                    "Not an email address.", Toast.LENGTH_SHORT).show();
            spinner.setVisibility(View.INVISIBLE);
            return;
        }
        if (!theUsername.equals("") && !thePassword.equals("")) {
            signIn(theUsername, thePassword);
        } else {
            Toast.makeText(LoginActivity.this,
                   "Please enter username and password",Toast.LENGTH_SHORT).show();
            spinner.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Start the sign-up activity.
     *
     * @param view
     */
    public void signUp(View view) {
        String name = username.getText().toString();
        Intent intent = new Intent(LoginActivity.this, NewUserActivity.class);
        intent.putExtra("USERNAME", name);
        LoginActivity.this.startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    /**
     * Sign in with google
     *
     * @param view
     */
    public void googleSignIn(View view) {
        spinner.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                spinner.setVisibility(View.VISIBLE);
            }
        }

    }

    /**
     * Authenticate Google with Firebase
     *
     * @param acct The Google Account
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            spinner.setVisibility(View.VISIBLE);
                            appLogin();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Google sign-in failed", Toast.LENGTH_SHORT).show();
                            spinner.setVisibility(View.VISIBLE);
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
        mAuth.signInWithEmailAndPassword(emailAddress, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(LoginActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                            appLogin();
                            spinner.setVisibility(View.GONE);
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Login failure",
                                    Toast.LENGTH_SHORT).show();
                            spinner.setVisibility(View.GONE);
                        }
                    }
                });
    }

    /**
     * Login to application and load hub activity
     */
    private void appLogin() {
        // Load new intent
        Intent intent = new Intent(LoginActivity.this, HubActivity.class);
        LoginActivity.this.startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
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

