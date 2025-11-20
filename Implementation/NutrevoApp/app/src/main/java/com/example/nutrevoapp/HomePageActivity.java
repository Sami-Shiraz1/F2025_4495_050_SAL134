package com.example.nutrevoapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomePageActivity extends AppCompatActivity {

    TextView tvWelcomeUser;
    RecyclerView rvHealthRecords;
    HealthRecordAdapter adapter;
    List<HealthRecord> recordList;

    CardView healthTipsCard, heartTipsCard, calorieCard, bmiCard, workoutCard;;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_activity);

        // Initialize views
        tvWelcomeUser = findViewById(R.id.tvWelcomeUser);
        rvHealthRecords = findViewById(R.id.rvHealthRecords);

        healthTipsCard = findViewById(R.id.HealthTips);
        heartTipsCard = findViewById(R.id.HeartTip);
        calorieCard = findViewById(R.id.calorieCalc);
        bmiCard = findViewById(R.id.BMI);
        workoutCard = findViewById(R.id.workoutRecommender);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 1️⃣ Display logged-in user name
        if (auth.getCurrentUser() != null) {
            String email = auth.getCurrentUser().getEmail();
            if (email != null && email.contains("@")) {
                String firstName = email.split("@")[0]; // Extract text before '@'
                // Capitalize first letter
                firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
                tvWelcomeUser.setText("Hello " + firstName + "!");
            } else {
                tvWelcomeUser.setText("Hello!");
            }
        } else {
            tvWelcomeUser.setText("Hello!");
        }


        // 2️⃣ Setup RecyclerView
        recordList = new ArrayList<>();
        adapter = new HealthRecordAdapter(recordList);
        rvHealthRecords.setLayoutManager(new LinearLayoutManager(this));
        rvHealthRecords.setAdapter(adapter);

        // 3️⃣ Fetch user's health records
        fetchHealthRecords();

        // 4️⃣ Setup click listeners for cards
        healthTipsCard.setOnClickListener(v ->
                startActivity(new Intent(HomePageActivity.this, HealthAIPredictorActivity.class)));

        heartTipsCard.setOnClickListener(v ->
                startActivity(new Intent(HomePageActivity.this, HeartDiseasePredictorActivity.class)));

        calorieCard.setOnClickListener(v ->
                startActivity(new Intent(HomePageActivity.this, CalorieCalculator.class)));

        bmiCard.setOnClickListener(v ->
                startActivity(new Intent(HomePageActivity.this, BmiCalculatorActivity.class)));

        workoutCard.setOnClickListener(v ->
                startActivity(new Intent(HomePageActivity.this, WorkoutRecommenderActivity.class)));
    }

    private void fetchHealthRecords() {
        if (auth.getCurrentUser() == null) return;

        String uid = auth.getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .collection("health_records")
                .orderBy("time")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    recordList.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String advice = doc.getString("advice");
                        long timestamp = doc.getLong("time") != null ? doc.getLong("time") : 0;

                        String formattedDate = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
                                .format(new Date(timestamp));

                        recordList.add(new HealthRecord(advice, formattedDate));
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch health records", Toast.LENGTH_SHORT).show();
                });
    }
}
