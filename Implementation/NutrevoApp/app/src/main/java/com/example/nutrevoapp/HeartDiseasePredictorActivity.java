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
import java.util.Random;

public class HeartDiseasePredictorActivity extends AppCompatActivity {

    EditText etAge, etGender, etBP, etChol, etBMI;
    TextView tvResult;
    Button btnPredict;
    Interpreter interpreter;

    // Min-max ranges based on typical healthy adult data
    private final float MIN_AGE = 25f, MAX_AGE = 80f;
    private final float MIN_BP = 80f, MAX_BP = 200f;
    private final float MIN_CHOL = 100f, MAX_CHOL = 300f;
    private final float MIN_BMI = 15f, MAX_BMI = 40f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_pred);

        etAge = findViewById(R.id.etAge);
        etGender = findViewById(R.id.etGender); // 0 = Male, 1 = Female
        etBP = findViewById(R.id.etBP);
        etChol = findViewById(R.id.etChol);
        etBMI = findViewById(R.id.etBMI);
        tvResult = findViewById(R.id.tvResult);
        btnPredict = findViewById(R.id.btnPredict);

        try {
            interpreter = new Interpreter(loadModelFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        btnPredict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                predictRisk();
            }
        });
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        FileInputStream fis = new FileInputStream(getAssets().openFd("heart_disease_risk.tflite").getFileDescriptor());
        FileChannel fileChannel = fis.getChannel();
        long startOffset = getAssets().openFd("heart_disease_risk.tflite").getStartOffset();
        long declaredLength = getAssets().openFd("heart_disease_risk.tflite").getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void predictRisk() {
        try {
            // Read inputs
            float age = Float.parseFloat(etAge.getText().toString());
            float gender = Float.parseFloat(etGender.getText().toString());
            float bp = Float.parseFloat(etBP.getText().toString());
            float chol = Float.parseFloat(etChol.getText().toString());
            float bmi = Float.parseFloat(etBMI.getText().toString());

            // Normalize inputs based on min-max ranges
            float ageNorm = (age - MIN_AGE) / (MAX_AGE - MIN_AGE);
            float bpNorm = (bp - MIN_BP) / (MAX_BP - MIN_BP);
            float cholNorm = (chol - MIN_CHOL) / (MAX_CHOL - MIN_CHOL);
            float bmiNorm = (bmi - MIN_BMI) / (MAX_BMI - MIN_BMI);

            float[][] input = new float[1][5];
            input[0][0] = ageNorm;
            input[0][1] = gender; // usually 0 or 1
            input[0][2] = bpNorm;
            input[0][3] = cholNorm;
            input[0][4] = bmiNorm;

            float[][] output = new float[1][3];
            interpreter.run(input, output);

            // Find max probability
            int maxIndex = 0;
            float maxValue = output[0][0];
            for (int i = 1; i < 3; i++) {
                if (output[0][i] > maxValue) {
                    maxValue = output[0][i];
                    maxIndex = i;
                }
            }

            String[] labels = {
                    "✅ Low Risk: Your heart looks healthy! Keep it up!",
                    "⚠️ Medium Risk: Pay attention to your lifestyle and diet.",
                    "❌ High Risk: Consult a doctor and focus on heart-healthy habits immediately!"
            };

            Random random = new Random();
            if (maxIndex == 2 && maxValue < 0.8) { // if high but not confident
                maxIndex = random.nextBoolean() ? 1 : 2; // sometimes medium
            }

            tvResult.setText("Heart Disease Risk: " + labels[maxIndex]);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numeric values!", Toast.LENGTH_SHORT).show();
        }
    }
}
