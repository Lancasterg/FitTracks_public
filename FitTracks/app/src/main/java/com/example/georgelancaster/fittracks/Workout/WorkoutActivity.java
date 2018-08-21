package com.example.georgelancaster.fittracks.Workout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.georgelancaster.fittracks.Authentication.LoginActivity;
import com.example.georgelancaster.fittracks.BaseActivity;
import com.example.georgelancaster.fittracks.Hub.HubActivity;
import com.example.georgelancaster.fittracks.Objects.Exercise;
import com.example.georgelancaster.fittracks.Objects.Workout;
import com.example.georgelancaster.fittracks.R;
import com.example.georgelancaster.fittracks.Tracking.TrackActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

/**
 * The workout activity, the destination on the left.
 */
public class WorkoutActivity extends BaseActivity {
    private DatabaseReference mDatabase;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Workout> workoutList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getInstance().getCurrentUser();
        workoutList = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new WorkoutAdapter(workoutList);
        //RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                sendMessage(position);
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
        getData();
        mRecyclerView.setAdapter(mAdapter);
        setNavigationListener();

        // Setup app bar
        //getSupportActionBar().setTitle("Select a Workout");
        setupAppBar();
        setTheActivity();
    }

    /**
     * Starts a new activity
     * @param position the position of the workout in the list.
     */
    private void sendMessage(int position){
        Workout workout = workoutList.get(position);
        Intent intent = new Intent(this, CompleteWorkout.class);
        intent.putExtra("theWorkout", workout);
        startActivity(intent);
        Toast.makeText(getApplicationContext(), workout.getName() + " is selected!", Toast.LENGTH_SHORT).show();
    }


    /**
     * Retrieves the data from the database
     */
    private void getData(){
        DatabaseReference ref = mDatabase.child("Database");//.child("Workouts");
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot userData = dataSnapshot.child("User").child(user.getUid())
                        .child("Workouts");

                ArrayList<String> keys = new ArrayList<>();
                for(DataSnapshot data : userData.getChildren()) {
                    keys.add(String.valueOf(data.getValue()));
                }
                workoutList.clear();
                DataSnapshot workoutSnapshot = dataSnapshot.child("Workouts");
                for(DataSnapshot data : workoutSnapshot.getChildren()) {
                    if (keys.contains(data.getKey())) {
                        Workout workout = new Workout();
                        workout.setName(String.valueOf(data.child("name").getValue()));
                        workout.setOwner(String.valueOf(data.child("owner").getValue()));
                        for (DataSnapshot exerciseData : data.child("exercises").getChildren()) {
                            Exercise exercise = new Exercise();
                            exercise.setName(String.valueOf(exerciseData.child("name").getValue()));
                            exercise.setReps(String.valueOf(exerciseData.child("reps").getValue()));
                            exercise.setSets(String.valueOf(exerciseData.child("sets").getValue()));
                            workout.add(exercise);
                        }
                        workoutList.add(workout);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }


}
