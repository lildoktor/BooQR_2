package com.example.nice_login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    TextView textToLogin;
    FirebaseAuth mAuth;
    Button buttonConfirm;
    EditText editTextEmail;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        textToLogin = findViewById(R.id.loginNow);
        buttonConfirm = findViewById(R.id.reset);
        progressBar = findViewById(R.id.progressBar);
        editTextEmail = findViewById(R.id.email);
        mAuth = FirebaseAuth.getInstance();

        buttonConfirm.setOnClickListener(view -> {
            String email = editTextEmail.getText().toString().trim();
            if (email.isEmpty()) {
                editTextEmail.setError("Email is required!");
                editTextEmail.requestFocus();
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextEmail.setError("Please provide valid email!");
                editTextEmail.requestFocus();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Check your email to reset your password!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Unless Firebase stopped working your email is wrong!", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            });
        });

        textToLogin.setOnClickListener(view -> {
            finish();
        });
    }
}