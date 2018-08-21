package com.example.georgelancaster.fittracks;

import org.junit.Test;

import com.example.georgelancaster.fittracks.Objects.Exercise;
import com.example.georgelancaster.fittracks.Objects.Workout;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTest {

    /**
     * Test the Workout and Exercise Objects
     * @throws Exception
     */
    @Test
    public void testWorkouts() throws Exception{

        // Build workout and Exercise objects
        Workout workout = new Workout();
        workout.setName("John");
        workout.setOwner("John");
        Exercise exercise = new Exercise();
        exercise.setName("AnExercise");
        exercise.setReps("5");
        exercise.setSets("10");
        workout.add(exercise);

        // Test Name
        assertEquals("John",workout.getName());
        // Test Owner
        assertEquals("John",workout.getName());
        // TestExercises
        assertEquals("AnExercise",workout.getExercises().get(0).getName());
        assertEquals("10",workout.getExercises().get(0).getSets());
        assertEquals("5",workout.getExercises().get(0).getReps());
    }
}