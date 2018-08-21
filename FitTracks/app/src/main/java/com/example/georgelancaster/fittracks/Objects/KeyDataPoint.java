package com.example.georgelancaster.fittracks.Objects;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;

import java.util.Date;

/**
 * Extension to DataPoint from the Android GraphView API.
 * It is used to store Firebase keys, so that datapoints can be referenced in the database.
 * Created by George on 06/04/2018.
 */
public class KeyDataPoint extends DataPoint {

    private String firebaseKey;

    public KeyDataPoint(double x, double y) {
        super(x, y);
    }

    public KeyDataPoint(Date x, double y) {
        super(x, y);
    }

    public String getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(String theKey) {
        this.firebaseKey = theKey;
    }

}
