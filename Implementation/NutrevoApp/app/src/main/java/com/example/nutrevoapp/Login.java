package com.example.nutrevoapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText inputUser, inputPass;
    private Button btnLogin;
    private UserDatabase dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputUser = findViewById(R.id.username_field);
        inputPass = findViewById(R.id.password_field);
        btnLogin = findViewById(R.id.login_button);

        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.login_button) {
            dbHelper = new UserDatabase(this);

            String userName = inputUser.getText().toString().trim();
            String userPass = inputPass.getText().toString().trim();

            boolean isValid = dbHelper.check(userName, userPass);

            if (isValid) {
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                Intent openRoutine = new Intent(Login.this, RoutineActivity.class);
                startActivity(openRoutine);
            } else {
                Toast.makeText(this, "Invalid Username or Password. Try again!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
