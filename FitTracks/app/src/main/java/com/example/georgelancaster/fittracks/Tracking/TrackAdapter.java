package com.example.georgelancaster.fittracks.Tracking;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.georgelancaster.fittracks.Objects.Workout;
import com.example.georgelancaster.fittracks.Objects.WorkoutEvent;
import com.example.georgelancaster.fittracks.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by George on 12/03/2018.
 */

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {

    public ArrayList<WorkoutEvent> eventList;

    public TrackAdapter(ArrayList<WorkoutEvent> theEventList) {
        this.eventList = theEventList;
    }

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_track_exercise, parent, false);

        return new TrackAdapter.TrackViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TrackViewHolder holder, int position) {
        WorkoutEvent theWorkout = eventList.get(position);
        holder.theWorkoutName.setText(theWorkout.getName());
        holder.theDate.setText(simpleDate(theWorkout.getDate()));
        holder.setOnclick(theWorkout);

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public String simpleDate(long longDate) {
        Date date = new Date(longDate);
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy");
        return df2.format(date);
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder {
        private TextView theWorkoutName;
        private TextView theDate;
        private CheckBox dropButton;
        private List<TextView> exerciseList;
        private List<TextView> setsList;
        private List<TextView> repsList;
        private List<TextView> weightList;
        private TableLayout exerciseTable;

        public TrackViewHolder(View view) {
            super(view);
            theWorkoutName = (TextView) view.findViewById(R.id.theWorkoutName);
            exerciseTable = (TableLayout) view.findViewById(R.id.exerciseTable);
            dropButton = (CheckBox) view.findViewById(R.id.dropdownButton);
            theDate = (TextView) view.findViewById(R.id.textDate);
            initRepsList(view);
            initSetsList(view);
            initExerciseList(view);
            initWeightList(view);
        }

        /**
         * Initialise the setsList
         * @param view the ViewHolders view
         */
        public void initRepsList(View view) {
            setsList = new ArrayList<>();
            setsList.add((TextView) view.findViewById(R.id.sets0));
            setsList.add((TextView) view.findViewById(R.id.sets1));
            setsList.add((TextView) view.findViewById(R.id.sets2));
            setsList.add((TextView) view.findViewById(R.id.sets3));
            setsList.add((TextView) view.findViewById(R.id.sets4));
            setsList.add((TextView) view.findViewById(R.id.sets5));
            setsList.add((TextView) view.findViewById(R.id.sets6));
        }
        /**
         * Initialise the repsList
         * @param view the ViewHolders view
         */
        public void initSetsList(View view) {
            repsList = new ArrayList<>();
            repsList.add((TextView) view.findViewById(R.id.reps0));
            repsList.add((TextView) view.findViewById(R.id.reps1));
            repsList.add((TextView) view.findViewById(R.id.reps2));
            repsList.add((TextView) view.findViewById(R.id.reps3));
            repsList.add((TextView) view.findViewById(R.id.reps4));
            repsList.add((TextView) view.findViewById(R.id.reps5));
            repsList.add((TextView) view.findViewById(R.id.reps6));
        }
        /**
         * Initialise the ExerciseList
         * @param view the ViewHolders view
         */
        public void initExerciseList(View view) {
            exerciseList = new ArrayList<>();
            exerciseList.add((TextView) view.findViewById(R.id.exercise0));
            exerciseList.add((TextView) view.findViewById(R.id.exercise1));
            exerciseList.add((TextView) view.findViewById(R.id.exercise2));
            exerciseList.add((TextView) view.findViewById(R.id.exercise3));
            exerciseList.add((TextView) view.findViewById(R.id.exercise4));
            exerciseList.add((TextView) view.findViewById(R.id.exercise5));
            exerciseList.add((TextView) view.findViewById(R.id.exercise6));
        }
        /**
         * Initialise the weightList
         * @param view the ViewHolders view
         */
        public void initWeightList(View view) {
            weightList = new ArrayList<>();
            weightList.add((TextView) view.findViewById(R.id.weight0));
            weightList.add((TextView) view.findViewById(R.id.weight1));
            weightList.add((TextView) view.findViewById(R.id.weight2));
            weightList.add((TextView) view.findViewById(R.id.weight3));
            weightList.add((TextView) view.findViewById(R.id.weight4));
            weightList.add((TextView) view.findViewById(R.id.weight5));
            weightList.add((TextView) view.findViewById(R.id.weight6));
        }

        /**
         *  Drop-down for the currently selected WorkoutEvent
         * @param theWorkout the current Workout
         */
        public void setOnclick(final Workout theWorkout) {
            dropButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (dropButton.isChecked()) {
                        for (int i = 0; i < theWorkout.getExercises().size(); i++) {
                            exerciseList.get(i).setVisibility(View.VISIBLE);
                            setsList.get(i).setVisibility(View.VISIBLE);
                            repsList.get(i).setVisibility(View.VISIBLE);
                            weightList.get(i).setVisibility(View.VISIBLE);
                            exerciseTable.setVisibility(View.VISIBLE);
                            exerciseList.get(i).setText(theWorkout.getExercises().get(i).getName());
                            setsList.get(i).setText(theWorkout.getExercises().get(i).getSets());
                            repsList.get(i).setText(theWorkout.getExercises().get(i).getReps());
                            weightList.get(i).setText(theWorkout.getExercises().get(i).getWeight());
                        }
                    } else if (!dropButton.isChecked()) {
                        for (int i = 0; i < theWorkout.getExercises().size(); i++) {
                            exerciseList.get(i).setVisibility(View.GONE);
                            setsList.get(i).setVisibility(View.GONE);
                            repsList.get(i).setVisibility(View.GONE);
                            weightList.get(i).setVisibility(View.GONE);
                            exerciseTable.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }


    }


}





