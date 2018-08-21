package com.example.georgelancaster.fittracks.Objects;

import java.io.Serializable;

/**
 * Created by George on 02/01/2018.
 * An exercise object.
 */

public class Exercise implements Serializable {

    private String theName;
    private String theSets;
    private String theReps;
    private String theWeight;

    public Exercise() {

    }

    public Exercise(String name, String sets, String reps) {
        theName = name;
        theSets = sets;
        theReps = reps;
    }

    public Exercise(String name, String sets, String reps, String weight) {
        theName = name;
        theSets = sets;
        theReps = reps;
        theWeight = weight;
    }

    public String getName() {
        return theName;
    }

    public String getReps() {
        return theReps;
    }

    public String getSets() {
        return theSets;
    }

    public String getWeight() {
        return theWeight;
    }

    public void setName(String theName) {
        this.theName = theName;
    }

    public void setReps(String theReps) {
        this.theReps = theReps;
    }

    public void setSets(String theSets) {
        this.theSets = theSets;
    }

    public void setTheWeight(String theWeight) {
        this.theWeight = theWeight;
    }
}
