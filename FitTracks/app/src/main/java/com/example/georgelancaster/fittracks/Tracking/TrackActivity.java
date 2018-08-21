package com.example.georgelancaster.fittracks.Tracking;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.georgelancaster.fittracks.Authentication.LoginActivity;
import com.example.georgelancaster.fittracks.BaseActivity;
import com.example.georgelancaster.fittracks.Hub.HubActivity;
import com.example.georgelancaster.fittracks.Objects.Exercise;
import com.example.georgelancaster.fittracks.Objects.WorkoutEvent;
import com.example.georgelancaster.fittracks.R;
import com.example.georgelancaster.fittracks.Workout.WorkoutActivity;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Track activity, the destination on the right
 */
public class TrackActivity extends BaseActivity {

    private ArrayList<WorkoutEvent> workoutEventList;
    private ArrayList<WorkoutEvent> eventList;
    private CompactCalendarView compactCalendarView;
    private TextView monthView;
    private RecyclerView.LayoutManager eventManager;
    private RecyclerView.Adapter eventAdapter;
    private RecyclerView append;
    //public static Activity theActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        setupAppBar();
        workoutEventList = new ArrayList<>();
        eventList = new ArrayList<>();
        monthView = (TextView) findViewById(R.id.monthTextView);
        setTheActivity();
        user = mAuth.getInstance().getCurrentUser();
        eventAdapter = new TrackAdapter(eventList);
        eventManager = new LinearLayoutManager(getApplicationContext());

        //Set up RecyclerView and its adapter
        append = (RecyclerView) findViewById(R.id.eventListView);
        append.setLayoutManager(eventManager);
        append.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        append.setItemAnimator(new DefaultItemAnimator());
        append.setAdapter(eventAdapter);
        append.setNestedScrollingEnabled(false);
        getWorkoutEvents();
        compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendarView);

        // Setup app bar
        getSupportActionBar().setTitle("Tracking");

        // define a listener to receive callbacks when certain events happen.
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {

            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> events = compactCalendarView.getEvents(dateClicked);
                eventList.clear();
                for (Event e : events) {
                    eventList.add((WorkoutEvent) e.getData());
                }
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                Log.d("tag", "Month was scrolled to: " + firstDayOfNewMonth);
                monthView.setText(getMonth(firstDayOfNewMonth.getMonth()));
            }
        });
        Date test = compactCalendarView.getFirstDayOfCurrentMonth();
        monthView.setText(getMonth(test.getMonth()));
        setNavigationListener();
        compactCalendarView.showNextMonth();
        compactCalendarView.showPreviousMonth();
        compactCalendarView.refreshDrawableState();
        compactCalendarView.setCurrentDate(new Date(System.currentTimeMillis()));
    }

    /**
     * Retrieve the WorkoutEvent objects form the database
     */
    public void getWorkoutEvents() {
        Query ref = FirebaseDatabase.getInstance().getReference().child("Database").child("User")
                .child(user.getUid()).child("completedWorkouts");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Retrieve data
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    WorkoutEvent event = new WorkoutEvent();
                    event.setName(String.valueOf(data.child("name").getValue()));
                    String dateString = String.valueOf(data.child("date").getValue());
                    event.setDate(Long.valueOf(dateString));
                    Log.v("theDate", new Date(Long.valueOf(dateString)).toString());

                    for (DataSnapshot exerciseData : data.child("exercises").getChildren()) {
                        Exercise exercise = new Exercise();
                        exercise.setName(String.valueOf(exerciseData.child("name").getValue()));
                        exercise.setReps(String.valueOf(exerciseData.child("reps").getValue()));
                        exercise.setSets(String.valueOf(exerciseData.child("sets").getValue()));
                        exercise.setTheWeight(String.valueOf(exerciseData.child("weight").getValue()));
                        event.add(exercise);
                    }
                    Event ev1 = new Event(Color.WHITE, event.getDate(), event);
                    compactCalendarView.addEvent(ev1);
                    workoutEventList.add(event);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    /**
     * Open the GraphActivity
     *
     * @param view
     */
    public void onClickBtnGraph(View view) {
        Intent intent = new Intent(this, GraphActivity.class);
        startActivity(intent);
    }

    /**
     * @param month the number for the month
     * @return the month for the date
     */
    public String getMonth(int month) {

        return new DateFormatSymbols().getMonths()[month];
    }


}
