package com.example.nutrevoapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etEmail, etPassword;
    Button btnSignup;
    TextView tvGoLogin;
    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignup = findViewById(R.id.btnSignup);
        tvGoLogin = findViewById(R.id.tvGoLogin);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnSignup.setOnClickListener(v -> registerUser());
        tvGoLogin.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
    }

    void registerUser() {
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String pass = etPassword.getText().toString();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, pass).addOnSuccessListener(authResult -> {
            String uid = auth.getCurrentUser().getUid();

            Map<String, Object> userData = new HashMap<>();
            userData.put("name", name);
            userData.put("email", email);

            db.collection("users").document(uid).set(userData).addOnSuccessListener(unused -> {
                Toast.makeText(this, "Account Created!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            });

        }).addOnFailureListener(e ->
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
