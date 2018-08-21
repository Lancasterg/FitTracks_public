package com.example.georgelancaster.fittracks.Hub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.georgelancaster.fittracks.Authentication.LoginActivity;
import com.example.georgelancaster.fittracks.BaseActivity;
import com.example.georgelancaster.fittracks.Objects.Exercise;
import com.example.georgelancaster.fittracks.Objects.Workout;
import com.example.georgelancaster.fittracks.R;
import com.example.georgelancaster.fittracks.Tracking.TrackActivity;
import com.example.georgelancaster.fittracks.Workout.WorkoutActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Hub activity, the middle destination.
 */
public class HubActivity extends BaseActivity {
    private DatabaseReference mDatabase;
    Button btn_create_workout;
    private TabHost tabHost;
    EditText tab1Edit;
    EditText tab2Edit;
    private RecyclerView tabList1;
    private RecyclerView tabList2;
    private ProgressDialog progressDialog;
    private RecyclerView.LayoutManager tabListManager1;
    private RecyclerView.LayoutManager tabListManager2;
    private RecyclerView.Adapter tabListAdapter1;
    private RecyclerView.Adapter tabListAdapter2;
    private ArrayList<Workout> myWorkoutList;           // Workout names for ListView
    private ArrayList<Workout> browseWorkoutList;       // Workouts names for ListView
    private ArrayList<Workout> subscribedWorkouts;      // List of subscribed workouts

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub);
        setupAppBar();
        user = mAuth.getInstance().getCurrentUser();
        btn_create_workout = (Button) findViewById(R.id.createWorkout);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        tabList1 = (RecyclerView) findViewById(R.id.tabList1);
        tabList2 = (RecyclerView) findViewById(R.id.tabList2);
        tab1Edit = (EditText) findViewById(R.id.edit_text_tab1);
        tab2Edit = (EditText) findViewById(R.id.edit_text_tab2);

        // Init ArrayLists
        myWorkoutList = new ArrayList<>();
        browseWorkoutList = new ArrayList<>();
        subscribedWorkouts = new ArrayList<>();
        tabListAdapter1 = new HubAdapter(myWorkoutList, subscribedWorkouts, findViewById(android.R.id.content).getContext());
        tabListAdapter2 = new HubAdapter(browseWorkoutList, subscribedWorkouts, findViewById(android.R.id.content).getContext());

        //My Workouts tab
        tabList1.setHasFixedSize(true);
        tabListManager1 = new LinearLayoutManager(getApplicationContext());
        tabList1.setLayoutManager(tabListManager1);
        tabList1.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        tabList1.setItemAnimator(new DefaultItemAnimator());
        tabList1.setAdapter(tabListAdapter1);

        // Browse Workouts tab
        tabList2.setHasFixedSize(true);
        tabListManager2 = new LinearLayoutManager(getApplicationContext());
        tabList2.setLayoutManager(tabListManager2);
        tabList2.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        tabList2.setItemAnimator(new DefaultItemAnimator());
        tabList2.setAdapter(tabListAdapter2);

        //Setup TabHost
        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        //Tab 1
        TabHost.TabSpec spec = tabHost.newTabSpec("Tab One");
        spec.setContent(R.id.tab1);
        spec.setIndicator("My Workouts");
        tabHost.addTab(spec);

        //Tab 2
        spec = tabHost.newTabSpec("Tab Two");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Browse Workouts");
        tabHost.addTab(spec);

        // Set tab text to white
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(getResources().getColor(R.color.white));
        }

        // Stops auto-focus of search
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setNavigationListener();

        // Setup app bar
        getSupportActionBar().setTitle("The Hub");
        setTheActivity();
        // load the workouts
        loadWorkouts();
    }

    /**
     * Load workouts on a separate thread and display a progress dialog.
     */
    public void loadWorkouts() {
        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Your workouts are loading..."); // Setting Message
        progressDialog.setTitle("Loading workouts"); // Setting Title
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);
        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {
                    getWorkouts(); // retrieve the workouts
                    sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        mThread.start();
    }

    /**
     * Retrieve the workouts from the database.
     **/
    public void getWorkouts() {
        final DatabaseReference ref = mDatabase.child("Database");
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                browseWorkoutList.clear();
                DataSnapshot workoutData = dataSnapshot.child("Workouts");
                // Retrieve all workouts from database
                for (DataSnapshot data : workoutData.getChildren()) {
                    Workout workout = new Workout();
                    workout.setId(String.valueOf(data.getKey()));
                    workout.setName(String.valueOf(data.child("name").getValue()));
                    workout.setOwner(String.valueOf(data.child("owner").getValue()));
                    for (DataSnapshot exerciseData : data.child("exercises").getChildren()) {
                        Exercise exercise = new Exercise();
                        exercise.setName(String.valueOf(exerciseData.child("name").getValue()));
                        exercise.setReps(String.valueOf(exerciseData.child("reps").getValue()));
                        exercise.setSets(String.valueOf(exerciseData.child("sets").getValue()));
                        workout.add(exercise);
                    }
                    browseWorkoutList.add(workout);
                }
                DataSnapshot userData = dataSnapshot.child("User").child(user.getUid())
                        .child("Workouts");
                ArrayList<String> keys = new ArrayList<>();
                for (DataSnapshot data : userData.getChildren()) {
                    keys.add(String.valueOf(data.getValue()));
                }
                myWorkoutList.clear();
                subscribedWorkouts.clear();
                // Retrieve my workouts from database
                for (DataSnapshot dataItem : workoutData.getChildren()) {
                    if (keys.contains(dataItem.getKey())) {
                        Workout workout = new Workout();
                        workout.setId(dataItem.getKey());
                        workout.setName(String.valueOf(dataItem.child("name").getValue()));
                        workout.setOwner(String.valueOf(dataItem.child("owner").getValue()));
                        for (DataSnapshot exerciseData : dataItem.child("exercises").getChildren()) {
                            Exercise exercise = new Exercise();
                            exercise.setName(String.valueOf(exerciseData.child("name").getValue()));
                            exercise.setReps(String.valueOf(exerciseData.child("reps").getValue()));
                            exercise.setSets(String.valueOf(exerciseData.child("sets").getValue()));
                            workout.add(exercise);
                        }
                        subscribedWorkouts.add(workout);
                        myWorkoutList.add(workout);
                    }
                }
                tabListAdapter1.notifyDataSetChanged();
                tabListAdapter2.notifyDataSetChanged();
                progressDialog.dismiss(); // dismiss progress dialog
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    /**
     * Start the intent to create a new workout.
     *
     * @param view the current view
     */
    public void startCreateWorkout(View view) {
        Intent intent = new Intent(this, CreateWorkout.class);
        startActivity(intent);
    }


}