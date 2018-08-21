package com.example.georgelancaster.fittracks;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.content.BroadcastReceiver;
import com.example.georgelancaster.fittracks.Authentication.LoginActivity;
import com.example.georgelancaster.fittracks.Hub.HubActivity;

import com.example.georgelancaster.fittracks.Objects.Workout;
import com.example.georgelancaster.fittracks.Tracking.TrackActivity;
import com.example.georgelancaster.fittracks.Workout.WorkoutActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by George Lancaster on 06/02/2018.
 * This activity is a base activity superclass which some activities in my app can extend.
 */


public class BaseActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    public static Activity theActivity;
    public FirebaseAuth mAuth;
    public FirebaseUser user;


    public void setTheActivity(){
        theActivity = this;
    }
    /**
     * The bottom navigation.
     */
    public void setNavigationListener() {
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Intent intent;
                        switch (item.getItemId()) {

                            case R.id.Hub:
                                if (BaseActivity.this instanceof HubActivity) {
                                    return true;
                                }
                                intent = new Intent(BaseActivity.this, HubActivity.class);

                                if (BaseActivity.this instanceof WorkoutActivity) {
                                    BaseActivity.this.startActivity(intent);
                                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                    return true;
                                } else {
                                    BaseActivity.this.startActivity(intent);
                                    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                                    return true;
                                }

                            case R.id.Workout:
                                if (BaseActivity.this instanceof WorkoutActivity) {
                                    return true;
                                }
                                intent = new Intent(BaseActivity.this, WorkoutActivity.class);
                                BaseActivity.this.startActivity(intent);
                                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                                return true;

                            case R.id.Track:
                                if (BaseActivity.this instanceof TrackActivity) {
                                    return true;
                                }
                                intent = new Intent(BaseActivity.this, TrackActivity.class);
                                BaseActivity.this.startActivity(intent);
                                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                Log.v("errorhere","slide");
                                return true;

                            default:
                                return true;
                        }
                    }
                });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); // Stops window from re
    }

    /**
     * Create the options menu.
     * @param menu the menu
     * @return true on success
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar_menu, menu);
        return true;
    }

    /**
     * The options menu for the top right menu.
     * @param item the menu item selected
     * @return true if action performed
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Logout:
                logout();
                return true;
        }
        return true;
    }

    /**
     * Logs the current user out of the application and returns them to the login screen.
     */
    public void logout(){
        FirebaseAuth.getInstance().signOut();
        if(TrackActivity.theActivity != null){
            TrackActivity.theActivity.finish();
        }
        if(WorkoutActivity.theActivity != null ){
            WorkoutActivity.theActivity.finish();
        }
        if(HubActivity.theActivity != null){
            HubActivity.theActivity.finish();
        }
        theActivity.finishAffinity();
        finish();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("finish",true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    /**
     * Set up the app bar. This removes the shadow between the app bar and the rest of the
     * application.
     */
    public void setupAppBar(){
        getSupportActionBar().setElevation(0);
    }

}
