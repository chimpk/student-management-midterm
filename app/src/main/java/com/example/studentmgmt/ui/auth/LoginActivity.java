package com.example.studentmgmt.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.studentmgmt.MainActivity;
import com.example.studentmgmt.R;
import com.example.studentmgmt.databinding.ActivityLoginBinding;
import com.example.studentmgmt.model.AppUser;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Authentication activity for username/password login.
 * Covers S01 (Default), S02 (Loading), S03 (Error), S27 (Dark).
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Hide action bar if present on Login screen
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        setupListeners();
        observeViewModel();
    }

    private void setupListeners() {
        binding.btnLogin.setOnClickListener(v -> {
            hideKeyboard();
            String identifier = binding.etIdentifier.getText() != null
                    ? binding.etIdentifier.getText().toString().trim() : "";
            String password = binding.etPassword.getText() != null
                    ? binding.etPassword.getText().toString() : "";

            viewModel.login(identifier, password);
        });

        // Nút Quên mật khẩu: Không tạo luồng reset password giả, hiển thị hướng dẫn liên hệ Quản trị viên
        binding.tvForgotPassword.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.forgot_password)
                    .setMessage(R.string.no_account_hint)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        });

        // Tự động ẩn lỗi khi người dùng gõ lại
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.layoutLoginError.setVisibility(View.GONE);
                binding.tilIdentifier.setError(null);
                binding.tilPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        binding.etIdentifier.addTextChangedListener(textWatcher);
        binding.etPassword.addTextChangedListener(textWatcher);
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, loading -> {
            boolean isLoading = Boolean.TRUE.equals(loading);
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnLogin.setEnabled(!isLoading);
            binding.btnLogin.setText(isLoading ? R.string.btn_logging_in : R.string.btn_login);
            binding.etIdentifier.setEnabled(!isLoading);
            binding.etPassword.setEnabled(!isLoading);
            binding.tvForgotPassword.setEnabled(!isLoading);
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.trim().isEmpty()) {
                binding.tvErrorMessage.setText(error);
                binding.layoutLoginError.setVisibility(View.VISIBLE);
                binding.tilIdentifier.setError(error);
            } else {
                binding.layoutLoginError.setVisibility(View.GONE);
                binding.tilIdentifier.setError(null);
            }
        });

        viewModel.getLoginOutcome().observe(this, outcome -> {
            if (outcome != null) {
                AppUser user = outcome.consumeUser();
                if (user != null) {
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
