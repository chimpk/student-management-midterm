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
 * Placeholder fragment for Student list and detail operations.
 * TODO(member-2): Student module and CRUD operations will be implemented by Member 2.
 */
public class StudentPlaceholderFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_student_placeholder, container, false);
    }
}
