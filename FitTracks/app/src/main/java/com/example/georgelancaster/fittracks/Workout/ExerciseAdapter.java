package com.example.georgelancaster.fittracks.Workout;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.georgelancaster.fittracks.Objects.Exercise;
import com.example.georgelancaster.fittracks.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by George on 15/01/2018.
 */

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.MyViewHolder> {

    private List<Exercise> exerciseList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public LinearLayout exerciseAppend;
        public CheckBox set1, set2, set3, set4, set5, set6, set7, set8;
        ArrayList<CheckBox> checkOrder;

        public MyViewHolder(View view) {
            super(view);
            checkOrder = new ArrayList<>();
            name = (TextView) view.findViewById(R.id.exerciseName);
            exerciseAppend = (LinearLayout) view.findViewById(R.id.exerciseAppend);
            set1 = (CheckBox) view.findViewById(R.id.set1);
            set2 = (CheckBox) view.findViewById(R.id.set2);
            set3 = (CheckBox) view.findViewById(R.id.set3);
            set4 = (CheckBox) view.findViewById(R.id.set4);
            set5 = (CheckBox) view.findViewById(R.id.set5);
            set6 = (CheckBox) view.findViewById(R.id.set6);
            set7 = (CheckBox) view.findViewById(R.id.set7);
            set8 = (CheckBox) view.findViewById(R.id.set8);
            checkOrder.add(set1);
            checkOrder.add(set2);
            checkOrder.add(set3);
            checkOrder.add(set4);
            checkOrder.add(set5);
            checkOrder.add(set6);
            checkOrder.add(set7);
            checkOrder.add(set8);
        }

        public void addButton(int pos, int reps) {
            CheckBox set = checkOrder.get(pos);
            set.setVisibility(View.VISIBLE);
            set.setBackgroundResource(getCheckboxID(reps));
        }
    }

    public ExerciseAdapter(List<Exercise> list) {
        this.exerciseList = list;
    }


    public ExerciseAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_exercise, parent, false);
        return new ExerciseAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ExerciseAdapter.MyViewHolder holder, int position) {
        Exercise exercise = exerciseList.get(position);
        int setsNum = Integer.valueOf(exercise.getSets());
        int repsNum = Integer.valueOf(exercise.getReps());

        // by default add 8 checkboxes.
        // Only set the ones we need to visible.

        for (int i = 0; i < setsNum; i++) {
            holder.addButton(i, repsNum);
        }
        holder.name.setText(exercise.getName());

    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    private static int getCheckboxID(int reps) {
        ArrayList<Integer> id = new ArrayList<>();
        id.add(R.drawable.checkbox1);
        id.add(R.drawable.checkbox2);
        id.add(R.drawable.checkbox3);
        id.add(R.drawable.checkbox4);
        id.add(R.drawable.checkbox5);
        id.add(R.drawable.checkbox6);
        id.add(R.drawable.checkbox7);
        id.add(R.drawable.checkbox8);
        id.add(R.drawable.checkbox9);
        id.add(R.drawable.checkbox10);
        id.add(R.drawable.checkbox11);
        id.add(R.drawable.checkbox12);
        return id.get(reps-1);
    }
}
