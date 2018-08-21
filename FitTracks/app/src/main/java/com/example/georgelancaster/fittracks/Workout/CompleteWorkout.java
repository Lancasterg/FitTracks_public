package com.example.georgelancaster.fittracks.Workout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.georgelancaster.fittracks.Objects.Exercise;
import com.example.georgelancaster.fittracks.Objects.Workout;
import com.example.georgelancaster.fittracks.Objects.WorkoutEvent;
import com.example.georgelancaster.fittracks.R;
import com.example.georgelancaster.fittracks.Tracking.TrackActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

/**
 * The activity for completing a workout. It is populated using CardViews from the
 * ExerciseAdapter class.
 *
 * When a workout is completed, this activity can submit it to the database.
 */

public class CompleteWorkout extends AppCompatActivity {
    private Workout workout;
    private ArrayList<Exercise> exercises;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_workout);
        // init user and database
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // retrieve workout from previous activity
        workout = (Workout) getIntent().getSerializableExtra("theWorkout");
        exercises = workout.getExercises();

        // init Layouts
        mRecyclerView = (RecyclerView) findViewById(R.id.exerciseRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ExerciseAdapter(exercises);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        // Stops auto-focus of search
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Set the title of app bar
        getSupportActionBar().setTitle(workout.getName());
    }

    /**
     * Submits the WorkoutEvent to the database.
     * @param view the view
     */
    public void finishWorkout(View view){
        // get database reference
        DatabaseReference ref = mDatabase.child("Database")
                                         .child("User").child(user.getUid())
                                         .child("completedWorkouts")
                                         .child(mDatabase.push().getKey());
        // Create the workout event
        WorkoutEvent event = new WorkoutEvent(  workout.getName(),
                                                getCompletedExercises(),
                                                System.currentTimeMillis());

        // Upload workout event to the database
        ref.child("name").setValue(event.getName());
        ref.child("date").setValue(System.currentTimeMillis());
        ref.child("exercises").setValue(event.getExercises());

        // If the TrackActivity is open, end it
        if(TrackActivity.theActivity != null){
            TrackActivity.theActivity.finish();
        }

        // Start the TrackActivity
        Intent intent = new Intent(this,TrackActivity.class);
        startActivity(intent);

    }

    /**
     * Retrieve the completed exercises.
     * Loops through all exercises in the workout and returns them in a list.
     * @return a list of completed exercises
     */
    private ArrayList<Exercise> getCompletedExercises(){
        ArrayList<Exercise> ret = new ArrayList<>();

        for (int i = 0; i < mRecyclerView.getChildCount(); i++){
            ArrayList<CheckBox> checkBoxes = new ArrayList<>();
            int setCount = 0;
            View v = mRecyclerView.getChildAt(i);
            checkBoxes.add((CheckBox) v.findViewById(R.id.set1));
            checkBoxes.add((CheckBox) v.findViewById(R.id.set2));
            checkBoxes.add((CheckBox) v.findViewById(R.id.set3));
            checkBoxes.add((CheckBox) v.findViewById(R.id.set4));
            checkBoxes.add((CheckBox) v.findViewById(R.id.set5));
            checkBoxes.add((CheckBox) v.findViewById(R.id.set6));
            checkBoxes.add((CheckBox) v.findViewById(R.id.set7));
            checkBoxes.add((CheckBox) v.findViewById(R.id.set8));

            // Loop through checkboxes in workout
            for (int j=0; j < checkBoxes.size(); j++){
                CheckBox box = checkBoxes.get(j);
                if(box.getVisibility( ) == View.VISIBLE){
                    if(box.isChecked()){
                        setCount++;
                    }
                }
            }

            TextView kgAmt = (TextView) v.findViewById(R.id.kg_amt);
            String theWeight = kgAmt.getText().toString();
            if (theWeight.equals("")){
                theWeight = "N/A";
            }
            // Add the exercise to the list of exercises
            ret.add(new Exercise(exercises.get(i).getName(),
                    String.valueOf(setCount),
                    exercises.get(i).getReps(),theWeight));
        }
        return ret;
    }

}
