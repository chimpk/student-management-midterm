package com.example.studentmgmt.ui.placeholder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.studentmgmt.R;
import com.example.studentmgmt.data.AuthRepositoryProvider;
import com.example.studentmgmt.model.AppUser;

/**
 * Placeholder fragment for User Profile.
 */
public class ProfilePlaceholderFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_placeholder, container, false);

        AppUser user = AuthRepositoryProvider.getRepository().getCurrentUser();
        if (user != null) {
            TextView tvUsername = view.findViewById(R.id.tvProfileUsername);
            TextView tvRole = view.findViewById(R.id.tvProfileRole);

            if (tvUsername != null) {
                tvUsername.setText(getString(R.string.placeholder_username_format, user.getUsername()));
            }
            if (tvRole != null) {
                tvRole.setText(getString(R.string.placeholder_role_format, user.getRole().toUpperCase(java.util.Locale.ROOT)));
            }
        }

        return view;
    }
}
