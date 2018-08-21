package com.example.georgelancaster.fittracks.Workout;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.georgelancaster.fittracks.Objects.Workout;
import com.example.georgelancaster.fittracks.R;
import java.util.List;

/**
 *
 * Created by George on 11/01/2018.
 *
 */

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.MyViewHolder> {

    private List<Workout> workoutList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
        }
    }


    public WorkoutAdapter(List<Workout> moviesList) {
        this.workoutList = moviesList;
    }


    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.workout_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Workout workout = workoutList.get(position);
        holder.title.setText(workout.getName());
    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }

}
