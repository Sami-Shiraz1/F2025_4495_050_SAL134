package com.example.nutrevoapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class HomePageActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    private CardView healthTips, exerciseTips, foodTips, medicineKit;
    private CardView bmiCalculator, nearbyHospitals, routine, aboutUs;
    private CardView calorieCalculator, settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_activity);

        drawerLayout = findViewById(R.id.drawerId);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize CardViews and set click listeners
        setupCardViews();
    }

    private void setupCardViews() {
        healthTips = findViewById(R.id.HealthTips);
        exerciseTips = findViewById(R.id.ExerciseTips);
        foodTips = findViewById(R.id.FoodTips);
        medicineKit = findViewById(R.id.MedKit);
        calorieCalculator = findViewById(R.id.calorieCalc);
        bmiCalculator = findViewById(R.id.BMI);
        nearbyHospitals = findViewById(R.id.Hospital);
        routine = findViewById(R.id.Routine);
        aboutUs = findViewById(R.id.About);
        settings = findViewById(R.id.Settings);

        // Set listeners
        healthTips.setOnClickListener(this);
        exerciseTips.setOnClickListener(this);
        foodTips.setOnClickListener(this);
        medicineKit.setOnClickListener(this);
        calorieCalculator.setOnClickListener(this);
        bmiCalculator.setOnClickListener(this);
        nearbyHospitals.setOnClickListener(this);
        routine.setOnClickListener(this);
        aboutUs.setOnClickListener(this);
        settings.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.HealthTips) {
            startActivity(new Intent(this, HealthTipsActivity.class));
        } else if (id == R.id.ExerciseTips) {
            startActivity(new Intent(this, ExerciseTipsActivity.class));
        } else if (id == R.id.FoodTips) {
            startActivity(new Intent(this, FoodTipsActivity.class));
        } else if (id == R.id.calorieCalc) {
            startActivity(new Intent(this, CalorieCalculator.class));
        } else if (id == R.id.BMI) {
            startActivity(new Intent(this, BmiCalculatorActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mnuitemon_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
