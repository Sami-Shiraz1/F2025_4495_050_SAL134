package com.example.nutrevoapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import org.tensorflow.lite.Interpreter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class WorkoutRecommenderActivity extends AppCompatActivity {

    Spinner spFitnessLevel, spGoal;
    SeekBar sbWeeklyTime;
    RadioGroup rgWorkoutType;
    Button btnRecommend;
    TextView tvWeeklyTime, tvWorkoutResult;
    Interpreter tflite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_recommender);

        spFitnessLevel = findViewById(R.id.spFitnessLevel);
        spGoal = findViewById(R.id.spGoal);
        sbWeeklyTime = findViewById(R.id.sbWeeklyTime);
        rgWorkoutType = findViewById(R.id.rgWorkoutType);
        btnRecommend = findViewById(R.id.btnRecommend);
        tvWeeklyTime = findViewById(R.id.tvWeeklyTime);
        tvWorkoutResult = findViewById(R.id.tvWorkoutResult);

        sbWeeklyTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvWeeklyTime.setText("Workout Time: " + progress + " hrs/week");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        try {
            tflite = new Interpreter(loadModelFile("workout_recommender.tflite"));
        } catch (IOException e) {
            e.printStackTrace();
            tvWorkoutResult.setText("Error loading model.");
        }

        btnRecommend.setOnClickListener(v -> recommendWorkout());
    }

    private MappedByteBuffer loadModelFile(String modelName) throws IOException {
        FileInputStream fis = new FileInputStream(getAssets().openFd(modelName).getFileDescriptor());
        FileChannel fc = fis.getChannel();
        long startOffset = getAssets().openFd(modelName).getStartOffset();
        long declaredLength = getAssets().openFd(modelName).getDeclaredLength();
        return fc.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void recommendWorkout() {
        int fitnessLevel = spFitnessLevel.getSelectedItemPosition();
        int goal = spGoal.getSelectedItemPosition();
        int weeklyTime = sbWeeklyTime.getProgress();

        int selectedId = rgWorkoutType.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Select workout type", Toast.LENGTH_SHORT).show();
            return;
        }
        int workoutType = selectedId == R.id.rbCardio ? 0 : selectedId == R.id.rbStrength ? 1 : 2;

        float[][] input = new float[1][4];
        input[0][0] = fitnessLevel / 2f;
        input[0][1] = goal / 2f;
        input[0][2] = weeklyTime / 10f;
        input[0][3] = workoutType / 2f;

        float[][] output = new float[1][3];
        tflite.run(input, output);

        int predictedClass = argMax(output[0]);
        tvWorkoutResult.setText(getRecommendation(predictedClass));
    }

    private int argMax(float[] arr) {
        int maxIndex = 0;
        float max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) { max = arr[i]; maxIndex = i; }
        }
        return maxIndex;
    }

    private String getRecommendation(int index) {
        String[] recs = {
                "🏃 Cardio: Running, cycling, HIIT 3-5 times/week.",
                "💪 Strength: Weight training 3-4 times/week.",
                "🧘 Flexibility: Yoga, Pilates, stretching sessions."
        };
        return recs[index];
    }
}
