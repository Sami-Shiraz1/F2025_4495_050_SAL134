package com.example.nutrevoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

public class ExerciseTipsActivity extends AppCompatActivity {

    private final int[] imageResources = {
            R.drawable.running, R.drawable.pushup, R.drawable.ropeskipping,
            R.drawable.cycling, R.drawable.swimming, R.drawable.pullup,
            R.drawable.stretching, R.drawable.squat, R.drawable.walking,
            R.drawable.climbing, R.drawable.weightlifting
    };

    private String[] exerciseNames;
    private String[] exerciseDescriptions;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_tips);

        gridView = findViewById(R.id.gridViewId);

        exerciseNames = getResources().getStringArray(R.array.excercise_names);
        exerciseDescriptions = getResources().getStringArray(R.array.excercise_description);

        CustomAdapter adapter = new CustomAdapter(this, exerciseNames, imageResources);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent descriptionIntent = new Intent(ExerciseTipsActivity.this, DescriptionActivity.class);
                descriptionIntent.putExtra("destext", exerciseDescriptions[position]);
                descriptionIntent.putExtra("desimage", imageResources[position]);
                startActivity(descriptionIntent);
            }
        });
    }
}
