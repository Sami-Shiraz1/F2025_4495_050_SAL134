package com.example.nutrevoapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DescriptionActivity extends AppCompatActivity {

    TextView destext;
    ImageView desimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.description);

        destext = findViewById(R.id.desTextViewId);
        desimage = findViewById(R.id.desImageViewId);

        Intent receivedIntent = getIntent();
        String textFromIntent = receivedIntent.getStringExtra("destext");
        int imageResourceId = receivedIntent.getIntExtra("desimage", 0);

        destext.setText(textFromIntent);
        if (imageResourceId != 0) {
            desimage.setImageResource(imageResourceId);
        }
    }
}
