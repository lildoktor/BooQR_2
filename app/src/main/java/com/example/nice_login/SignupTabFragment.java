package com.example.nice_login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class SignupTabFragment extends Fragment {

    EditText editTextEmail, editTextPassword, editTextConfirmPassword;
    ProgressBar progressBar;
    Button signupBtn;
    FirebaseAuth fAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextEmail = view.findViewById(R.id.signup_email);
        editTextPassword = view.findViewById(R.id.signup_password);
        editTextConfirmPassword = view.findViewById(R.id.signup_confirm);
        progressBar = view.findViewById(R.id.progressBar);
        signupBtn = view.findViewById(R.id.signup_button);
        fAuth = FirebaseAuth.getInstance();

        signupBtn.setOnClickListener(view1 -> {
            String emailInput = editTextEmail.getText().toString();
            String passwordInput = editTextPassword.getText().toString();
            String confirmPasswordInput = editTextConfirmPassword.getText().toString();

            if (emailInput.isEmpty()) {
                editTextEmail.setError("Email is required");
                editTextEmail.requestFocus();
                return;
            }
            if (passwordInput.isEmpty()) {
                editTextPassword.setError("Password is required");
                editTextPassword.requestFocus();
                return;
            }
            if (confirmPasswordInput.isEmpty()) {
                editTextConfirmPassword.setError("Confirm Password is required");
                editTextConfirmPassword.requestFocus();
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                editTextEmail.setError("Please enter a valid email");
                editTextEmail.requestFocus();
                return;
            }
            if (passwordInput.length() < 6) {
                editTextPassword.setError("Password must be at least 6 characters");
                editTextPassword.requestFocus();
                return;
            }
            if (!passwordInput.equals(confirmPasswordInput)) {
                editTextConfirmPassword.setError("Password does not match");
                editTextConfirmPassword.requestFocus();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            fAuth.createUserWithEmailAndPassword(emailInput, passwordInput).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(getActivity(), EmailVerification.class);
                    startActivity(intent);
                    if (getActivity() != null)
                        getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            });
        });

    }
}