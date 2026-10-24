package com.example.studentmgmt.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmgmt.R;
import com.example.studentmgmt.data.AuthRepositoryProvider;
import com.example.studentmgmt.databinding.ActivityAuthTerminalBinding;

/**
 * Terminal authentication state activity representing:
 * - S80: Session Expired (TYPE_SESSION_EXPIRED)
 * - S81: Account Locked (TYPE_ACCOUNT_LOCKED)
 *
 * Ensures back navigation CANNOT return to protected screens.
 */
public class AuthTerminalActivity extends AppCompatActivity {

    public static final String EXTRA_TERMINAL_TYPE = "extra_terminal_type";
    public static final int TYPE_SESSION_EXPIRED = 80;
    public static final int TYPE_ACCOUNT_LOCKED = 81;

    public static Intent createIntent(Context context, int type) {
        Intent intent = new Intent(context, AuthTerminalActivity.class);
        intent.putExtra(EXTRA_TERMINAL_TYPE, type);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    private ActivityAuthTerminalBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthTerminalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Always sign out locally
        AuthRepositoryProvider.getRepository().signOut();

        int type = getIntent().getIntExtra(EXTRA_TERMINAL_TYPE, TYPE_SESSION_EXPIRED);
        renderState(type);

        binding.btnBackToLogin.setOnClickListener(v -> navigateToLogin());

        // Pressing back directly routes to login and never back into protected app
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToLogin();
            }
        });
    }

    private void renderState(int type) {
        if (type == TYPE_ACCOUNT_LOCKED) {
            // S81
            binding.ivTerminalIcon.setImageResource(R.drawable.ic_block);
            binding.tvTerminalTitle.setText(R.string.account_locked_title);
            binding.tvTerminalDesc.setText(R.string.account_locked_desc);
            binding.tvTerminalNote.setText(R.string.account_access_unavailable);
        } else {
            // S80
            binding.ivTerminalIcon.setImageResource(R.drawable.ic_lock_clock);
            binding.tvTerminalTitle.setText(R.string.session_expired_title);
            binding.tvTerminalDesc.setText(R.string.session_expired_desc);
            binding.tvTerminalNote.setText(R.string.session_reauth_note);
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
