package com.example.georgelancaster.fittracks.Hub;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.georgelancaster.fittracks.Objects.Workout;
import com.example.georgelancaster.fittracks.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by George on 12/02/2018.
 */

public class HubAdapter extends RecyclerView.Adapter<HubAdapter.MyViewHolder> {
    private List<Workout> workoutList;
    private List<Workout> subscribedWorkouts;
    private List<Workout> ownedWorkouts;
    private Context parent;

    public HubAdapter(List<Workout> theWorkoutList) {
        this.workoutList = theWorkoutList;
    }

    public HubAdapter(List<Workout> theWorkoutList, List<Workout> theSubscribedList, Context theParent) {
        this.workoutList = theWorkoutList;
        this.subscribedWorkouts = theSubscribedList;
        this.ownedWorkouts = new ArrayList<>();
        this.parent = theParent;

        // Collect all owned workouts
        for (Workout w : theWorkoutList) {
            if (w.getOwner().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                this.ownedWorkouts.add(w);
            }
        }
    }

    public HubAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_browse, parent, false);
        return new HubAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Workout workout = workoutList.get(position);
        holder.setButtons(workout);
        holder.title.setText(workout.getName());
        holder.setOnclick(workout);
        holder.setOnclickSubButton(position, workout);
        holder.setOnClickDeleteButton(workout);

    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, workout;
        private CheckBox dropButton;
        private CheckBox subButton;
        private List<TextView> exerciseList;
        private List<TextView> setsList;
        private List<TextView> repsList;
        private TableLayout exerciseTable;
        private Button deleteButton;
        DatabaseReference mDatabase;
        FirebaseUser user;

        /**
         * Initialise the ViewHolder
         *
         * @param view for the holder
         */
        public MyViewHolder(View view) {
            super(view);
            dropButton = (CheckBox) view.findViewById(R.id.dropdownButton);
            exerciseTable = (TableLayout) view.findViewById(R.id.exerciseTable);
            subButton = (CheckBox) view.findViewById(R.id.subButton);
            user = FirebaseAuth.getInstance().getCurrentUser();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            deleteButton = (Button) view.findViewById(R.id.delete_workout);
            initExerciseList(view);
            initSetsList(view);
            initRepsList(view);
        }

        /**
         * Initialise the reps
         *
         * @param view the cardview
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
         * Initialise the sets
         *
         * @param view the cardview
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
         * Initialise the exercises
         *
         * @param view the cardview
         */
        public void initExerciseList(View view) {
            exerciseList = new ArrayList<>();
            title = (TextView) view.findViewById(R.id.theWorkoutName);
            exerciseList.add((TextView) view.findViewById(R.id.exercise0));
            exerciseList.add((TextView) view.findViewById(R.id.exercise1));
            exerciseList.add((TextView) view.findViewById(R.id.exercise2));
            exerciseList.add((TextView) view.findViewById(R.id.exercise3));
            exerciseList.add((TextView) view.findViewById(R.id.exercise4));
            exerciseList.add((TextView) view.findViewById(R.id.exercise5));
            exerciseList.add((TextView) view.findViewById(R.id.exercise6));
        }

        /**
         * Sets the button to subscribed or un-subscribed.
         *
         * @param workout the subbed/unsubbed workout
         */
        public void setButtons(Workout workout) {
            if (subscribedWorkouts != null) {
                Boolean subscribed = false;
                for (Workout w : subscribedWorkouts) {
                    if (workout.getId().equals(w.getId())) {
                        subscribed = true;
                    }

                }
                if (subscribed) {
                    subButton.setText(R.string.unsubscribe);
                    subButton.setOnCheckedChangeListener(null);
                    subButton.setChecked(true);
                    subButton.setBackgroundResource(R.drawable.round_button_grey);
                } else {
                    subButton.setText(R.string.subscribe);
                    subButton.setOnCheckedChangeListener(null);
                    subButton.setChecked(false);
                    subButton.setBackgroundResource(R.drawable.round_button);

                }
            }

        }

        /**
         * Set the onClick for the subscribe button for the workout.
         *
         * @param pos
         * @param theWorkout the workout selected
         */
        public void setOnclickSubButton(int pos, final Workout theWorkout) {
            subButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!subButton.isChecked()) {
                        mDatabase.child("Database").child("User").child(user.getUid()).child("Workouts")
                                .child(theWorkout.getId()).removeValue();
                        subButton.setText(R.string.subscribe);
                        subButton.setBackgroundResource(R.drawable.round_button);
                    }
                    if (subButton.isChecked()) {
                        mDatabase.child("Database").child("User").child(user.getUid()).child("Workouts")
                                .child(theWorkout.getId()).setValue(theWorkout.getId());
                        subButton.setText(R.string.unsubscribe);
                        subButton.setBackgroundResource(R.drawable.round_button_grey);
                    }
                }
            });
        }

        /**
         * Show or hide the details of the expandable cardview.
         *
         * @param theWorkout the workout in the list.
         */
        public void setOnclick(final Workout theWorkout) {
            dropButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (dropButton.isChecked()) {
                        //Hide
                        for (int i = 0; i < theWorkout.getExercises().size(); i++) {
                            exerciseList.get(i).setVisibility(View.VISIBLE);
                            setsList.get(i).setVisibility(View.VISIBLE);
                            repsList.get(i).setVisibility(View.VISIBLE);
                            exerciseTable.setVisibility(View.VISIBLE);
                            subButton.setVisibility(View.VISIBLE);
                            exerciseList.get(i).setText(theWorkout.getExercises().get(i).getName());
                            setsList.get(i).setText(theWorkout.getExercises().get(i).getSets());
                            repsList.get(i).setText(theWorkout.getExercises().get(i).getReps());
                            if (theWorkout.getOwner().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                deleteButton.setVisibility(View.VISIBLE);
                            }
                        }
                    } else if (!dropButton.isChecked()) {
                        //Show
                        for (int i = 0; i < theWorkout.getExercises().size(); i++) {
                            exerciseList.get(i).setVisibility(View.GONE);
                            setsList.get(i).setVisibility(View.GONE);
                            repsList.get(i).setVisibility(View.GONE);
                            exerciseTable.setVisibility(View.GONE);
                            subButton.setVisibility(View.GONE);
                            if (theWorkout.getOwner().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                deleteButton.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            });
        }

        /**
         * Deletes a workout. Only the workout owner can delete a workout.
         *
         * @param workout the workout to delete
         */
        public void setOnClickDeleteButton(final Workout workout) {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Context context = view.getContext();
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    //if yes, delete from database
                                    mDatabase.child("Database").child("Workouts").child(workout.getId()).removeValue();
                                    Toast.makeText(context, workout.getName() + " has been deleted", Toast.LENGTH_SHORT).show();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    return;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), R.style.AlertDialogTheme);
                    builder.setMessage(String.format("Are you sure you want to delete %s?", workout.getName())).setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();

                }
            });
        }
    }
}
