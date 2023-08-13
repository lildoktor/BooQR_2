package com.example.nice_login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class LoginTabFragment extends Fragment {

    TextView forgotPassword;
    EditText editTextEmail, editTextPassword;
    ProgressBar progressBar;
    Button loginBtn;
    FirebaseAuth fAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        forgotPassword = view.findViewById(R.id.forgot_password);
        editTextEmail = view.findViewById(R.id.login_email);
        editTextPassword = view.findViewById(R.id.login_password);
        progressBar = view.findViewById(R.id.progressBar);
        loginBtn = view.findViewById(R.id.login_button);
        fAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(view1 -> {
            String emailInput = editTextEmail.getText().toString();
            String passwordInput = editTextPassword.getText().toString();

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
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                editTextEmail.setError("Please provide valid email!");
                editTextEmail.requestFocus();
                return;
            }
            if (editTextPassword.length() < 6) {
                editTextPassword.setError("Min password length should be 6 characters!");
                editTextPassword.requestFocus();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            fAuth.signInWithEmailAndPassword(emailInput, passwordInput).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    if (getActivity() != null)
                        getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), "Failed to login! Please check your credentials", Toast.LENGTH_LONG).show();
                    editTextEmail.requestFocus();
                }
                progressBar.setVisibility(View.GONE);
            });
        });

        forgotPassword.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), ForgotPasswordActivity.class);
            startActivity(intent);
        });

    }

}