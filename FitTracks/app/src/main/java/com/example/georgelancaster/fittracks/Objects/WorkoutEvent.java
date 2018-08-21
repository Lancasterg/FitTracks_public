package com.example.georgelancaster.fittracks.Objects;


import java.util.ArrayList;

/**
 * Created by George on 15/01/2018.
 * Workout event is an extension to workout with added dates.
 */

public class WorkoutEvent extends Workout {
    private Long date;

    public WorkoutEvent() {
    }

    public WorkoutEvent(String theWorkoutName, ArrayList<Exercise> exerciseEvents, Long theDate) {
        this.name = theWorkoutName;
        this.exercises = exerciseEvents;
        this.date = theDate;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long theDate) {
        date = theDate;
    }


}
