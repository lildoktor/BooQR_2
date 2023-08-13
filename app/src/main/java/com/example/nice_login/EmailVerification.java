package com.example.nice_login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class EmailVerification extends AppCompatActivity {

    TextView resend;
    Button logout;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        mAuth = FirebaseAuth.getInstance();
        resend = findViewById(R.id.resend);
        logout = findViewById(R.id.signup_button_verify);

        mAuth.getCurrentUser().reload().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                checkEmailVerification();
                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task1 -> {
                });
            } else {
                Log.e("EmailVerification", "onCreate: " + task.getException());
            }
        });
        resend.setOnClickListener(v -> {
            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(EmailVerification.this, "Email sent!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EmailVerification.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        logout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(EmailVerification.this, LoginSignUpActivity.class));
            finish();
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.getCurrentUser().reload().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                checkEmailVerification();
            } else {
                Log.e("EmailVerification", "onResume: " + task.getException());
            }
        });
    }

    void checkEmailVerification() {
        if (mAuth.getCurrentUser().isEmailVerified()) {
            startActivity(new Intent(EmailVerification.this, MainActivity.class));
            finish();
        } else {
            Log.e("EmailVerification", "checkEmailVerification: " + mAuth.getCurrentUser().isEmailVerified());
        }
    }
}