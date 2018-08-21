package com.example.georgelancaster.fittracks.Hub;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.georgelancaster.fittracks.Objects.Exercise;
import com.example.georgelancaster.fittracks.Objects.Workout;
import com.example.georgelancaster.fittracks.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * The activity to create new workouts.
 */

public class CreateWorkout extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private LinearLayout append;
    private FirebaseUser user;
    private EditText workoutName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_workout);
        append = (LinearLayout) findViewById(R.id.append);
        workoutName = (EditText) findViewById(R.id.workoutName);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getSupportActionBar().setTitle("Create a Workout");
    }

    /**
     * Add an exercise to the view.
     * Inflates a new layout card to the view. This can be used to build a workout through the
     * addition of new exercises.
     * @param view the current view
     */
    public void add(View view) {
        LayoutInflater inflater = LayoutInflater.from(this);
        CardView card = (CardView) inflater.inflate(R.layout.card_create_exercise, null, false);
        LinearLayout lin = (LinearLayout) card.getChildAt(0);
        AutoCompleteTextView textView = (AutoCompleteTextView) lin.getChildAt(0);
        Spinner sets = (Spinner) lin.getChildAt(1);
        Spinner reps = (Spinner) lin.getChildAt(2);

        //Set the drop-down list for the sets
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Sets, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sets.setAdapter(adapter);

        //Set the drop-down list for the reps
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.Reps, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reps.setAdapter(adapter2);

        // Set the string array for the autocomplete of exercises
        String[] exercises = getResources().getStringArray(R.array.exercises_array);
        ArrayAdapter<String> exerciseAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, exercises);
        textView.setAdapter(exerciseAdapter);
        Button del = (Button) lin.getChildAt(3);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDelete(view);
            }
        });
        append.addView(card);
    }

    /**
     * Saves a workout to the database.
     * It works by gathering all children created by the add function and using the information to
     * create a workout object. The information from the workout object is saved to the database.
     * @param view the current view.
     */
    public void saveWorkout(View view){
        Workout workout = new Workout();
        if(workoutName.getText().toString().equals("")){
            Toast.makeText(CreateWorkout.this,"Please enter a name for your workout",Toast.LENGTH_SHORT).show();
            return;
        }
        if(append.getChildCount()<1){
            Toast.makeText(CreateWorkout.this,"You need at least one exercise",Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = 0; i < append.getChildCount(); i++){
            CardView card = (CardView) append.getChildAt(i);
            String[] list = new String[3];
            LinearLayout lin = (LinearLayout) card.getChildAt(0);
            AutoCompleteTextView exerciseName = (AutoCompleteTextView) lin.getChildAt(0);
            list[0] = exerciseName.getText().toString();
            // Retrieve data from children
            for (int j = 1; j < lin.getChildCount()-1; j++){
                Spinner edit = (Spinner) lin.getChildAt(j);
                list[j] = edit.getSelectedItem().toString();
                if(edit.getSelectedItem().equals("Sets")){
                    Toast.makeText(CreateWorkout.this,"Please select a number of sets",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(edit.getSelectedItem().equals("Reps")){
                    Toast.makeText(CreateWorkout.this,"Please select a number of reps",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (exerciseName.equals("")){
                    Toast.makeText(CreateWorkout.this,"Please enter a name for exercise",Toast.LENGTH_SHORT).show();
                    return;
                }

            }
            Exercise exercise = new Exercise(list[0],list[1],list[2]);
            workout.add(exercise);
            workout.setName(workoutName.getText().toString());
            workout.setOwner(user.getEmail());
        }
        // Add to the database
        String key = mDatabase.push().getKey();
        DatabaseReference reference = mDatabase.child("Database").child("Workouts").child(key);
        reference.child("owner").setValue(workout.getOwner());
        reference.child("exercises").setValue(workout.getExercises());
        reference.child("name").setValue(workout.getName());
        mDatabase.child("Database").child("User").child(user.getUid()).child("Workouts")
                .child(key).setValue(key);
        Intent intent = new Intent(this,HubActivity.class);
        startActivity(intent);
    }

    /**
     * Dynamically deletes an entry
     * @param view the view
     */
    public void onDelete(View view) {
        CardView card = (CardView) view.getParent().getParent();
        append.removeView(card);
    }

    /**
     * Override onBackPressed to ensure it goes to the HubActivity.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HubActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}

