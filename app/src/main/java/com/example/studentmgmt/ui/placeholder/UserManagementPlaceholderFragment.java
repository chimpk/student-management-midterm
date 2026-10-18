package com.example.studentmgmt.ui.placeholder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.studentmgmt.R;

/**
 * Placeholder fragment for User Management destination (Admin only).
 * Full CRUD and lock features will be implemented in Task 5 (T05/T06).
 */
public class UserManagementPlaceholderFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_management_placeholder, container, false);
    }
}
