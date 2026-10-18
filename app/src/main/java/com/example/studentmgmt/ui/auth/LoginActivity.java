package com.example.studentmgmt.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.studentmgmt.MainActivity;
import com.example.studentmgmt.databinding.ActivityLoginBinding;
import com.example.studentmgmt.model.AppUser;

/**
 * Authentication activity for username/password login.
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        setupListeners();
        observeViewModel();
    }

    private void setupListeners() {
        binding.btnLogin.setOnClickListener(v -> {
            hideKeyboard();
            String identifier = binding.etIdentifier.getText() != null
                    ? binding.etIdentifier.getText().toString() : "";
            String password = binding.etPassword.getText() != null
                    ? binding.etPassword.getText().toString() : "";

            viewModel.login(identifier, password);
        });
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, loading -> {
            boolean isLoading = Boolean.TRUE.equals(loading);
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnLogin.setEnabled(!isLoading);
            binding.etIdentifier.setEnabled(!isLoading);
            binding.etPassword.setEnabled(!isLoading);
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.trim().isEmpty()) {
                binding.tvErrorMessage.setText(error);
                binding.tvErrorMessage.setVisibility(View.VISIBLE);
            } else {
                binding.tvErrorMessage.setVisibility(View.GONE);
            }
        });

        viewModel.getLoginOutcome().observe(this, outcome -> {
            if (outcome != null) {
                AppUser user = outcome.consumeUser();
                if (user != null) {
                    // Navigate to MainActivity only once per outcome; survives activity recreation
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    if (outcome.hasWarning()) {
                        intent.putExtra(MainActivity.EXTRA_LOGIN_WARNING, outcome.getWarningMessage());
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}
