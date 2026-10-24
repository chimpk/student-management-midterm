package com.example.studentmgmt.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.studentmgmt.R;
import com.example.studentmgmt.databinding.BottomSheetStudentSortBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * BottomSheet dialog fragment covering S13: Student sorting criteria selection.
 */
public class StudentSortBottomSheetDialogFragment extends BottomSheetDialogFragment {

    public interface OnSortSelectedListener {
        void onSortSelected(StudentListViewModel.SortCriteria criteria);
    }

    private BottomSheetStudentSortBinding binding;
    private StudentListViewModel.SortCriteria initialCriteria = StudentListViewModel.SortCriteria.NAME_ASC;
    private OnSortSelectedListener listener;

    public static StudentSortBottomSheetDialogFragment newInstance(
            StudentListViewModel.SortCriteria current,
            OnSortSelectedListener listener) {
        StudentSortBottomSheetDialogFragment fragment = new StudentSortBottomSheetDialogFragment();
        fragment.initialCriteria = current != null ? current : StudentListViewModel.SortCriteria.NAME_ASC;
        fragment.listener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetStudentSortBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Pre-check current criteria
        switch (initialCriteria) {
            case NAME_ASC:
                binding.rbSortNameAsc.setChecked(true);
                break;
            case NAME_DESC:
                binding.rbSortNameDesc.setChecked(true);
                break;
            case MSSV_ASC:
                binding.rbSortMssvAsc.setChecked(true);
                break;
            case BIRTH_ASC:
                binding.rbSortBirthAsc.setChecked(true);
                break;
        }

        binding.btnApplySort.setOnClickListener(v -> {
            StudentListViewModel.SortCriteria selected = StudentListViewModel.SortCriteria.NAME_ASC;
            int checkedId = binding.rgSortOptions.getCheckedRadioButtonId();

            if (checkedId == R.id.rbSortNameDesc) {
                selected = StudentListViewModel.SortCriteria.NAME_DESC;
            } else if (checkedId == R.id.rbSortMssvAsc) {
                selected = StudentListViewModel.SortCriteria.MSSV_ASC;
            } else if (checkedId == R.id.rbSortBirthAsc) {
                selected = StudentListViewModel.SortCriteria.BIRTH_ASC;
            }

            if (listener != null) {
                listener.onSortSelected(selected);
            }
            dismiss();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
