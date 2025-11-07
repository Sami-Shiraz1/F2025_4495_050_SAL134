package com.example.nutrevoapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import org.tensorflow.lite.Interpreter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HealthAIPredictorActivity extends AppCompatActivity {

    SeekBar sbSleep, sbWater, sbExercise, sbAge;
    RadioGroup rgSteps;
    Spinner spStress;
    Button btnPredict, btnStats;
    TextView tvResult, tvSleepValue, tvWaterValue, tvExerciseValue, tvAgeValue;
    Interpreter tflite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_ai);

        sbSleep = findViewById(R.id.sbSleep);
        sbWater = findViewById(R.id.sbWater);
        sbExercise = findViewById(R.id.sbExercise);
        sbAge = findViewById(R.id.sbAge);
        rgSteps = findViewById(R.id.rgSteps);
        spStress = findViewById(R.id.spStress);
        btnPredict = findViewById(R.id.btnPredict);
        btnStats = findViewById(R.id.btnStats);
        tvResult = findViewById(R.id.tvResult);
        tvSleepValue = findViewById(R.id.tvSleepValue);
        tvWaterValue = findViewById(R.id.tvWaterValue);
        tvExerciseValue = findViewById(R.id.tvExerciseValue);
        tvAgeValue = findViewById(R.id.tvAgeValue);

        // Update labels dynamically
        sbSleep.setOnSeekBarChangeListener(simpleSeekListener(tvSleepValue, "Sleep: %d hrs"));
        sbWater.setOnSeekBarChangeListener(simpleSeekListener(tvWaterValue, "Water: %d L"));
        sbExercise.setOnSeekBarChangeListener(simpleSeekListener(tvExerciseValue, "Exercise: %d min"));
        sbAge.setOnSeekBarChangeListener(simpleSeekListener(tvAgeValue, "Age: %d yrs"));

        try {
            tflite = new Interpreter(loadModelFile());
        } catch (Exception e) {
            e.printStackTrace();
            tvResult.setText("Error loading model.");
        }

        btnPredict.setOnClickListener(v -> predictHealthTip());
        btnStats.setOnClickListener(v -> showUserStats());
    }

    private SeekBar.OnSeekBarChangeListener simpleSeekListener(TextView tv, String format) {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { tv.setText(String.format(format, progress)); }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        };
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        FileInputStream fis = new FileInputStream(getAssets().openFd("health_predictor_updated.tflite").getFileDescriptor());
        FileChannel fc = fis.getChannel();
        long startOffset = getAssets().openFd("health_predictor_updated.tflite").getStartOffset();
        long declaredLength = getAssets().openFd("health_predictor_updated.tflite").getDeclaredLength();
        return fc.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void predictHealthTip() {
        float sleep = sbSleep.getProgress();
        float water = sbWater.getProgress();
        float exercise = sbExercise.getProgress();
        float age = sbAge.getProgress();

        int selectedId = rgSteps.getCheckedRadioButtonId();
        if(selectedId == -1){ Toast.makeText(this,"Select steps",Toast.LENGTH_SHORT).show(); return; }
        float steps = selectedId == R.id.rbSteps1 ? 2000 :
                selectedId == R.id.rbSteps2 ? 5000 :
                        selectedId == R.id.rbSteps3 ? 9000 : 13000;

        String stressChoice = spStress.getSelectedItem().toString();
        float stress = stressChoice.equals("Low") ? 3 : stressChoice.equals("Medium") ? 6 : 9;

        float[][] input = new float[1][6]; // 6 features
        input[0][0] = sleep / 12f;
        input[0][1] = steps / 15000f;
        input[0][2] = stress / 10f;
        input[0][3] = water / 5f;
        input[0][4] = exercise / 120f;
        input[0][5] = age / 100f;

        float[][] output = new float[1][3];
        tflite.run(input, output);

        int predictedClass = argMax(output[0]);
        String result = interpretPredictionDetailed(predictedClass, sleep, steps, stress, water, exercise, age);
        tvResult.setText(result);
    }

    private int argMax(float[] arr) {
        int maxIndex = 0;
        float max = arr[0];
        for (int i = 1; i < arr.length; i++) if (arr[i] > max) { max = arr[i]; maxIndex = i; }
        return maxIndex;
    }

    private String interpretPredictionDetailed(int index, float sleep, float steps, float stress,
                                               float water, float exercise, float age) {
        String[] goodTips = {"💪 Excellent health!", "🙂 Great habits!", "🏃‍♂️ Keep active!"};
        String[] moderateTips = {"⚡ You’re doing okay, hydrate more.", "🧘 Balance rest & work.", "🙂 Try a short walk!"};
        String[] needsRestTips = {"⚠️ You need rest!", "😴 Take time off.", "💤 Sleep & relax!"};
        Random r = new Random();
        String base = index==0?goodTips[r.nextInt(3)]:index==1?moderateTips[r.nextInt(3)]:needsRestTips[r.nextInt(3)];

        // Add feature-based advice
        String advice = "";
        if(sleep < 6) advice += " 💤 Sleep more.";
        if(steps < 5000) advice += " 🚶 Walk more.";
        if(stress > 7) advice += " 🧘 Relax more.";
        if(water < 2) advice += " 💧 Drink more water.";
        if(exercise < 30) advice += " 🏋️ Exercise more.";
        if(age > 45) advice += " ⚠️ Extra care for age.";

        return base + advice;
    }

    private void showUserStats() {
        float sleep = sbSleep.getProgress();
        float water = sbWater.getProgress();
        float exercise = sbExercise.getProgress();
        float age = sbAge.getProgress();

        int selectedId = rgSteps.getCheckedRadioButtonId();
        float steps = selectedId == R.id.rbSteps1 ? 2000 :
                selectedId == R.id.rbSteps2 ? 5000 :
                        selectedId == R.id.rbSteps3 ? 9000 : 13000;

        String stressChoice = spStress.getSelectedItem().toString();
        float stress = stressChoice.equals("Low") ? 3 : stressChoice.equals("Medium") ? 6 : 9;

        View chartView = getLayoutInflater().inflate(R.layout.dialog_chart, null);
        BarChart chart = chartView.findViewById(R.id.barChart);
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1, sleep));
        entries.add(new BarEntry(2, steps/1000));
        entries.add(new BarEntry(3, stress));
        entries.add(new BarEntry(4, water));
        entries.add(new BarEntry(5, exercise/10));
        entries.add(new BarEntry(6, age/10));

        BarDataSet set = new BarDataSet(entries, "Health Metrics");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData data = new BarData(set);
        chart.setData(data);
        chart.getDescription().setEnabled(false);
        chart.invalidate();

        String analysis = generateAIInsights(sleep, steps, stress, water, exercise, age);

        new AlertDialog.Builder(this)
                .setTitle("📊 My Health Stats")
                .setView(chartView)
                .setMessage(analysis)
                .setPositiveButton("OK", null)
                .show();
    }

    private String generateAIInsights(float sleep, float steps, float stress, float water, float exercise, float age){
        String msg = "💬 Insights:\n";
        if(sleep < 6) msg += "• Sleep more hours.\n";
        if(steps < 5000) msg += "• Walk more daily.\n";
        if(stress > 7) msg += "• Relax and manage stress.\n";
        if(water < 2) msg += "• Drink more water.\n";
        if(exercise < 30) msg += "• Add more exercise.\n";
        if(age > 45) msg += "• Take extra care of health.\n";
        if(msg.equals("💬 Insights:\n")) msg += "• Excellent overall! Keep it up! 💪";
        return msg;
    }
}
