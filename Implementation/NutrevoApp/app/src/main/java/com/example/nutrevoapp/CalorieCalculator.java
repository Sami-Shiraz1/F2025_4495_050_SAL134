package com.example.nutrevoapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CalorieCalculator extends AppCompatActivity {

    // Arrays for spinner data
    private String[] breakfastItems, breakfastQty, lunchItems, lunchQty, dinnerItems, dinnerQty, snackItems;

    // Spinner widgets
    private Spinner spBreakfastItem, spBreakfastQty, spLunchItem, spLunchQty, spDinnerItem, spDinnerQty, spSnacks;

    // UI components
    private Button btnCalculate;
    private TextView txtResult;

    // Method to return calorie values
    private double getCalories(String item) {
        switch (item) {
            case "None": return 0;
            case "White Bread": return 2.65;
            case "Brown Bread": return 2.93;
            case "Cereals": return 3.79;
            case "Fruit Salad": return 0.5;
            case "Cooked Rice": return 1.3;
            case "Fried Rice": return 1.63;
            case "Pasta": return 1.31;
            case "Potato": return 0.77;
            case "Chicken": return 2.39;
            case "Pack of Potato Crackers": return 118;
            case "Pack of Digestive Biscuits": return 707;
            case "50gm": return 50;
            case "250gm": return 250;
            case "500gm": return 500;
            default: return 0;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie_calculator);

        // Load array data from resources
        breakfastItems = getResources().getStringArray(R.array.breakfast_items);
        breakfastQty   = getResources().getStringArray(R.array.breakfast_amount);
        lunchItems     = getResources().getStringArray(R.array.lunch_items);
        lunchQty       = getResources().getStringArray(R.array.lunch_amount);
        dinnerItems    = getResources().getStringArray(R.array.dinner_items);
        dinnerQty      = getResources().getStringArray(R.array.dinner_amount);
        snackItems     = getResources().getStringArray(R.array.snacks);

        // Initialize views
        spBreakfastItem = findViewById(R.id.breakfast_item);
        spBreakfastQty  = findViewById(R.id.breakfast_amount);
        spLunchItem     = findViewById(R.id.lunch_item);
        spLunchQty      = findViewById(R.id.lunch_amount);
        spDinnerItem    = findViewById(R.id.dinner_item);
        spDinnerQty     = findViewById(R.id.dinner_amount);
        spSnacks        = findViewById(R.id.snacks_item);
        btnCalculate    = findViewById(R.id.calculate);
        txtResult       = findViewById(R.id.result);

        // Setup adapters
        ArrayAdapter<String> breakfastItemAdapter = new ArrayAdapter<>(this, R.layout.view_setup, R.id.array_string_view, breakfastItems);
        ArrayAdapter<String> breakfastQtyAdapter  = new ArrayAdapter<>(this, R.layout.view_setup, R.id.array_string_view, breakfastQty);
        ArrayAdapter<String> lunchItemAdapter     = new ArrayAdapter<>(this, R.layout.view_setup, R.id.array_string_view, lunchItems);
        ArrayAdapter<String> lunchQtyAdapter      = new ArrayAdapter<>(this, R.layout.view_setup, R.id.array_string_view, lunchQty);
        ArrayAdapter<String> dinnerItemAdapter    = new ArrayAdapter<>(this, R.layout.view_setup, R.id.array_string_view, dinnerItems);
        ArrayAdapter<String> dinnerQtyAdapter     = new ArrayAdapter<>(this, R.layout.view_setup, R.id.array_string_view, dinnerQty);
        ArrayAdapter<String> snackAdapter         = new ArrayAdapter<>(this, R.layout.view_setup, R.id.array_string_view, snackItems);

        // Attach adapters to spinners
        spBreakfastItem.setAdapter(breakfastItemAdapter);
        spBreakfastQty.setAdapter(breakfastQtyAdapter);
        spLunchItem.setAdapter(lunchItemAdapter);
        spLunchQty.setAdapter(lunchQtyAdapter);
        spDinnerItem.setAdapter(dinnerItemAdapter);
        spDinnerQty.setAdapter(dinnerQtyAdapter);
        spSnacks.setAdapter(snackAdapter);

        // Calculate button listener
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double breakfast = getCalories(spBreakfastItem.getSelectedItem().toString()) *
                        getCalories(spBreakfastQty.getSelectedItem().toString());

                double lunch = getCalories(spLunchItem.getSelectedItem().toString()) *
                        getCalories(spLunchQty.getSelectedItem().toString());

                double dinner = getCalories(spDinnerItem.getSelectedItem().toString()) *
                        getCalories(spDinnerQty.getSelectedItem().toString());

                double snacks = getCalories(spSnacks.getSelectedItem().toString());

                double totalCalories = breakfast + lunch + dinner + snacks;

                txtResult.setText("Total Estimated Calories: " + totalCalories + " kcal");
            }
        });
    }
}
