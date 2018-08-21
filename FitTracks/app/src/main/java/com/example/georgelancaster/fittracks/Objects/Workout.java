package com.example.georgelancaster.fittracks.Objects;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by George Lancaster on 14/12/2017.
 * Workout contains a list of Exercise objects with reference to an exercise name and rep scheme.
 */

public class Workout implements Serializable {
    protected String name;
    private String owner;
    private String id;

    protected ArrayList<Exercise> exercises = new ArrayList<>();

    public Workout() {
    }

    public Workout(String theWorkoutName, String theOwner) {
        this.name = theWorkoutName;
        this.exercises = new ArrayList<>();
        this.owner = theOwner;
    }

    public Workout(String theWorkoutName, String theOwner, ArrayList<Exercise> theExercises) {
        this.name = theWorkoutName;
        this.exercises = theExercises;
        this.owner = theOwner;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public ArrayList<Exercise> getExercises() {
        return exercises;
    }

    public void setName(String workoutName) {
        this.name = workoutName;
    }

    public void setOwner(String theOwner) {
        this.owner = theOwner;
    }

    public void setExercises(ArrayList<Exercise> theExercises) {
        this.exercises = theExercises;
    }

    public void add(Exercise e) {
        this.exercises.add(e);
    }

    public String getId() {
        return id;
    }

    public void setId(String theId) {
        this.id = theId;
    }

}
