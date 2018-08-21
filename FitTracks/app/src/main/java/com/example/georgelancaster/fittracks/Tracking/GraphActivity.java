package com.example.georgelancaster.fittracks.Tracking;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.georgelancaster.fittracks.Objects.KeyDataPoint;
import com.example.georgelancaster.fittracks.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;
import java.util.Date;

public class GraphActivity extends AppCompatActivity {
    private com.jjoe64.graphview.GraphView graph;
    private FirebaseUser user;
    private DatabaseReference mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_view);
        getSupportActionBar().setElevation(0); // No gradient between action bar and toolbar.
        getSupportActionBar().setTitle("Graph of Progress");

        //Init class variables
        user = FirebaseAuth.getInstance().getCurrentUser();
        graph = (com.jjoe64.graphview.GraphView) findViewById(R.id.graph);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Set up Graph
        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Time");
        gridLabel.setVerticalAxisTitle("Body weight (Kg)");
        graph.setBackgroundColor(getResources().getColor(R.color.white));
        graph.getViewport().setMinY(20);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.performClick();
        graph.getViewport().setYAxisBoundsManual(true);

        graph.getViewport().setMaxX(System.currentTimeMillis() + (604800000 * 5));
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        gridLabel.setPadding(16);
        graph.getGridLabelRenderer().setNumHorizontalLabels(4);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getApplicationContext()));
        loadData();
        graph.performClick();
    }

    /**
     * Add a new body weight event to the graph.
     *
     * @param view
     */
    public void addEvent(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(GraphActivity.this, R.style.AlertDialogTheme);
        alertDialog.setTitle("Track Bodyweight");
        alertDialog.setMessage("Enter weight in Kg");
        final EditText input = new EditText(GraphActivity.this);
        input.getBackground().mutate().setColorFilter(getResources().getColor(R.color.caldroid_black), PorterDuff.Mode.SRC_ATOP);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String res = input.getText().toString();
                        if (!res.equals("") && res != null) {
                            String key = mDatabase.push().getKey();
                            DatabaseReference ref = mDatabase.child("Database").child("User").child(user.getUid()).child("Event").child(key);
                            ref.child("Date").setValue(ServerValue.TIMESTAMP);
                            ref.child("Weight").setValue(Double.valueOf(res));
                        } else {
                            Toast.makeText(GraphActivity.this, "Enter valid weight", Toast.LENGTH_LONG);
                        }
                    }
                });
        alertDialog.setNegativeButton("Back",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    /**
     * Load data from FireBase.
     * Uses an addValueEventListener to retrieve data.
     */
    public void loadData() {
        Query ref = FirebaseDatabase.getInstance().getReference().child("Database").child("User")
                .child(user.getUid()).child("Event");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                graph.removeAllSeries();
                ArrayList<DataPoint> genData = new ArrayList<>();
                double maxWeight = 21;
                long minDate = System.currentTimeMillis();
                long maxDate = 0;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Double dWeight = data.child("Weight").getValue(Double.class);
                    Long dateLong = data.child("Date").getValue(Long.class);
                    if (dWeight != null || dateLong != null) { // can not be null
                        Date date = new Date(dateLong);
                        if (dWeight == null) {
                            dWeight = 0.0;
                        }
                        if (dateLong < minDate) {   // find the smallest date
                            minDate = dateLong;
                        }
                        if (dateLong > maxDate) {   // find the largest date
                            maxDate = dateLong;
                        }
                        KeyDataPoint d = new KeyDataPoint(date, dWeight.doubleValue());
                        d.setFirebaseKey(data.getKey());
                        genData.add(d);
                        if (dWeight > maxWeight) {
                            maxWeight = dWeight;
                        }
                    }
                }

                DataPoint[] theData = new DataPoint[genData.size()];
                genData.toArray(theData);
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(theData);
                series.setThickness(8);
                series.setDrawDataPoints(true);
                series.setDataPointsRadius(25);
                // Plot graph
                graph.addSeries(series);
                graph.getViewport().setMaxY(maxWeight + 10);
                graph.getViewport().setMinX(minDate);
                graph.getViewport().setMaxX(maxDate);
                graph.getGridLabelRenderer().setHumanRounding(false);
                graph.getGridLabelRenderer().setPadding(96);
                graph.performClick();
                if (theData.length < 2) {
                    graph.getViewport().setMinX(maxDate);
                    graph.getViewport().setMaxX(System.currentTimeMillis());
                }
                series.setOnDataPointTapListener(new OnDataPointTapListener() {
                    @Override
                    public void onTap(Series series, DataPointInterface dataPoint) {
                        deleteAlert(dataPoint);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    /**
     * Deletes a body weight event from the System.
     *
     * @param dataPoint KeyDataPoint to delete
     */
    public void deleteAlert(final DataPointInterface dataPoint) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //if yes, delete from database
                        KeyDataPoint data = (KeyDataPoint) dataPoint;
                        mDatabase.child("Database").child("User").child(user.getUid()).child("Event").child(data.getFirebaseKey()).removeValue();
                        Toast.makeText(GraphActivity.this, "Event has been deleted", Toast.LENGTH_SHORT).show();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        return;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(GraphActivity.this, R.style.AlertDialogTheme);
        builder.setMessage("Are you sure you want to delete the event?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}