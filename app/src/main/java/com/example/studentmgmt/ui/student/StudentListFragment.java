package com.example.studentmgmt.ui.student;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentmgmt.R;
import com.example.studentmgmt.data.AuthRepositoryProvider;
import com.example.studentmgmt.databinding.DialogStudentAddEditBinding;
import com.example.studentmgmt.databinding.FragmentStudentListBinding;
import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.model.Student;
import com.example.studentmgmt.model.UiState;
import com.example.studentmgmt.ui.navigation.RoleNavigationPolicy;
import com.example.studentmgmt.utils.StudentCsvHelper;
import com.example.studentmgmt.utils.StudentDataValidation;
import com.example.studentmgmt.utils.ValidationUtils;
import com.example.studentmgmt.utils.VietnameseTextInputFormatter;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Student list screen conforming to S07, S12, S13, S14, S24-S26, S29.
 * Supports realtime list, search, faculty chips, sort sheet, undo delete, and RBAC.
 */
public class StudentListFragment extends Fragment implements StudentAdapter.OnStudentClickListener {

    private FragmentStudentListBinding binding;
    private StudentListViewModel viewModel;
    private StudentAdapter adapter;
    private boolean isEmployee = false;
    private boolean canManageStudents;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStudentListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(StudentListViewModel.class);
        canManageStudents = currentUserCanManageStudents();

        AppUser currentUser = AuthRepositoryProvider.getRepository().getCurrentUser();
        isEmployee = currentUser != null && currentUser.isEmployee();

        setupRecyclerView();
        setupSearchAndActions(currentUser);
        setupFilterChips();
        observeViewModel();
    }

    private void setupRecyclerView() {
        adapter = new StudentAdapter(this);
        adapter.setCanManageStudents(canManageStudents);
        adapter.setReadOnly(!canManageStudents);
        binding.rvStudents.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvStudents.setAdapter(adapter);

        binding.includeErrorState.btnRetry.setOnClickListener(v -> viewModel.retry());

        if (isEmployee) {
            binding.fabAddStudent.setVisibility(View.GONE);
            binding.includeEmptyState.layoutEmptyActions.setVisibility(View.GONE);
        } else {
            binding.fabAddStudent.setVisibility(View.VISIBLE);
            binding.includeEmptyState.btnEmptyAdd.setOnClickListener(v -> showAddStudentDialog());
            binding.includeEmptyState.btnEmptyImport.setOnClickListener(v ->
                    Navigation.findNavController(requireView()).navigate(R.id.nav_import_export));
        }
    }

    private void setupSearchAndActions(AppUser currentUser) {
        binding.toolbarStudentList.inflateMenu(R.menu.main_menu);
        MenuItem usersItem = binding.toolbarStudentList.getMenu().findItem(R.id.action_users);
        usersItem.setVisible(RoleNavigationPolicy.shouldShowMenuItem(
                currentUser == null ? null : currentUser.getRole(), RoleNavigationPolicy.MENU_USERS));
        binding.toolbarStudentList.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_logout) {
                if (getActivity() instanceof com.example.studentmgmt.MainActivity) {
                    ((com.example.studentmgmt.MainActivity) getActivity()).performLogout();
                }
                return true;
            } else if (item.getItemId() == R.id.action_users) {
                androidx.navigation.Navigation.findNavController(requireView()).navigate(R.id.nav_user_management);
                return true;
            } else if (item.getItemId() == R.id.action_profile) {
                androidx.navigation.Navigation.findNavController(requireView()).navigate(R.id.nav_profile);
                return true;
            } else if (item.getItemId() == R.id.action_history) {
                androidx.navigation.Navigation.findNavController(requireView()).navigate(R.id.nav_login_history);
                return true;
            }
            return false;
        });

        binding.fabAddStudent.setVisibility(canManageStudents ? View.VISIBLE : View.GONE);

        binding.etSearchStudent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearchQuery(s != null ? s.toString() : "");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        if (!isEmployee) {
            binding.fabAddStudent.setOnClickListener(v -> showAddStudentDialog());
        }

        binding.btnExportCsv.setOnClickListener(v -> exportCsv());
        binding.btnImportCsv.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigate(R.id.nav_import_export));

        binding.btnSortSheet.setOnClickListener(v -> {
            StudentSortBottomSheetDialogFragment sheet = StudentSortBottomSheetDialogFragment.newInstance(
                    viewModel.getSortCriteria(),
                    criteria -> {
                        viewModel.setSortCriteria(criteria);
                        updateCountSortHeader(viewModel.getStudentListState().getValue());
                    });
            sheet.show(getChildFragmentManager(), "student_sort_sheet");
        });
    }

    private void setupFilterChips() {
        binding.chipFilterAll.setOnClickListener(v -> viewModel.setFacultyFilter(null));
        binding.chipFilterIt.setOnClickListener(v -> viewModel.setFacultyFilter("Công nghệ thông tin"));
        binding.chipFilterCs.setOnClickListener(v -> viewModel.setFacultyFilter("Khoa học máy tính"));
        binding.chipFilterSe.setOnClickListener(v -> viewModel.setFacultyFilter("Kỹ thuật phần mềm"));
    }

    private void observeViewModel() {
        viewModel.getStudentListState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            if (state.isLoading()) {
                binding.includeLoadingSkeleton.getRoot().setVisibility(View.VISIBLE);
                binding.rvStudents.setVisibility(View.GONE);
                binding.includeEmptyState.getRoot().setVisibility(View.GONE);
                binding.includeErrorState.getRoot().setVisibility(View.GONE);
            } else if (state.isContent()) {
                binding.includeLoadingSkeleton.getRoot().setVisibility(View.GONE);
                binding.rvStudents.setVisibility(View.VISIBLE);
                binding.includeEmptyState.getRoot().setVisibility(View.GONE);
                binding.includeErrorState.getRoot().setVisibility(View.GONE);
                adapter.setStudents(state.getData());
                updateCountSortHeader(state);
            } else if (state.isEmpty()) {
                binding.includeLoadingSkeleton.getRoot().setVisibility(View.GONE);
                binding.rvStudents.setVisibility(View.GONE);
                binding.includeEmptyState.getRoot().setVisibility(View.VISIBLE);
                binding.includeErrorState.getRoot().setVisibility(View.GONE);
                updateCountSortHeader(state);
            } else if (state.isError()) {
                binding.includeLoadingSkeleton.getRoot().setVisibility(View.GONE);
                binding.rvStudents.setVisibility(View.GONE);
                binding.includeEmptyState.getRoot().setVisibility(View.GONE);
                binding.includeErrorState.getRoot().setVisibility(View.VISIBLE);
                binding.includeErrorState.tvErrorDesc.setText(state.getErrorMessage());
            }
        });

        viewModel.getActionSuccessMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                viewModel.clearActionMessages();
            }
        });

        viewModel.getActionErrorMessage().observe(getViewLifecycleOwner(), err -> {
            if (err != null) {
                Snackbar.make(binding.getRoot(), err, Snackbar.LENGTH_LONG).show();
                viewModel.clearActionMessages();
            }
        });

        viewModel.getLastDeletedStudentEvent().observe(getViewLifecycleOwner(), student -> {
            if (student != null) {
                Snackbar.make(binding.getRoot(), getString(R.string.snackbar_deleted_student_format, student.getName()), Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_undo, v -> viewModel.undoDelete())
                        .show();
            }
        });
    }

    private void updateCountSortHeader(UiState<List<Student>> state) {
        int count = (state != null && state.isContent() && state.getData() != null) ? state.getData().size() : 0;
        String sortLabel;
        switch (viewModel.getSortCriteria()) {
            case NAME_ASC:
                sortLabel = getString(R.string.sort_name_asc);
                break;
            case NAME_DESC:
                sortLabel = getString(R.string.sort_name_desc);
                break;
            case MSSV_ASC:
                sortLabel = getString(R.string.sort_mssv_asc);
                break;
            case BIRTH_ASC:
                sortLabel = getString(R.string.sort_birth_asc);
                break;
            default:
                sortLabel = getString(R.string.sort_name_asc);
                break;
        }
        binding.tvStudentCountSort.setText(getString(R.string.student_count_sort_synced_format, count, sortLabel));
    }

    @Override
    public void onStudentClick(Student student) {
        Bundle args = new Bundle();
        args.putString("arg_student_id", student.getStudentId());
        try {
            Navigation.findNavController(requireView()).navigate(R.id.nav_student_detail, args);
        } catch (Exception e) {
            StudentDetailFragment detailFragment = StudentDetailFragment.newInstance(student.getStudentId());
            detailFragment.show(getChildFragmentManager(), "student_detail");
        }
    }

    @Override
    public void onStudentEdit(Student student) {
        if (!canManageStudents) return;
        showEditStudentDialog(student);
    }

    @Override
    public void onStudentDelete(Student student) {
        if (!canManageStudents) return;
        showDeleteConfirmDialog(student);
    }

    private void showAddStudentDialog() {
        if (!canManageStudents) return;
        DialogStudentAddEditBinding dialogBinding = DialogStudentAddEditBinding.inflate(getLayoutInflater());
        dialogBinding.tvDialogTitle.setText(R.string.title_add_student);
        dialogBinding.tvDialogSubtitle.setText(R.string.subtitle_add_student);
        dialogBinding.tvMssvImmutableNote.setVisibility(View.GONE);

        setupDatePicker(dialogBinding);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogBinding.getRoot())
                .create();

        dialogBinding.btnCancelStudent.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnSaveStudent.setOnClickListener(v -> {
            String id = dialogBinding.etStudentId.getText() != null ? dialogBinding.etStudentId.getText().toString().trim() : "";
            String name = dialogBinding.etStudentName.getText() != null ? VietnameseTextInputFormatter.capitalizeWords(dialogBinding.etStudentName.getText().toString().trim()) : "";
            String dob = dialogBinding.etStudentDob.getText() != null ? dialogBinding.etStudentDob.getText().toString().trim() : "";
            String className = dialogBinding.etStudentClass.getText() != null ? dialogBinding.etStudentClass.getText().toString().trim() : "";
            String faculty = dialogBinding.etStudentFaculty.getText() != null ? VietnameseTextInputFormatter.capitalizeWords(dialogBinding.etStudentFaculty.getText().toString().trim()) : "";
            String email = dialogBinding.etStudentEmail != null && dialogBinding.etStudentEmail.getText() != null ? dialogBinding.etStudentEmail.getText().toString().trim() : "";

            String validationError = StudentDataValidation.validateStudent(
                    id, name, dob, className, faculty);
            if (validationError != null) {
                Snackbar.make(dialogBinding.getRoot(), validationError, Snackbar.LENGTH_LONG).show();
                return;
            }

            if (!email.isEmpty() && !ValidationUtils.isValidEmail(email)) {
                dialogBinding.tilStudentEmail.setError(getString(R.string.error_invalid_email));
                return;
            } else {
                dialogBinding.tilStudentEmail.setError(null);
            }

            Student newStudent = new Student(id, name, dob, className, faculty);
            viewModel.addStudent(newStudent);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showEditStudentDialog(Student student) {
        if (!canManageStudents) return;
        DialogStudentAddEditBinding dialogBinding = DialogStudentAddEditBinding.inflate(getLayoutInflater());
        dialogBinding.tvDialogTitle.setText(R.string.title_edit_student);
        dialogBinding.tvDialogSubtitle.setText(R.string.subtitle_edit_student);

        // Immutable primary key (S06 / S32)
        dialogBinding.etStudentId.setText(student.getStudentId());
        dialogBinding.etStudentId.setEnabled(false);
        dialogBinding.tvMssvImmutableNote.setVisibility(View.VISIBLE);

        dialogBinding.etStudentName.setText(student.getName());
        dialogBinding.etStudentDob.setText(student.getDateOfBirth());
        dialogBinding.etStudentClass.setText(student.getClassName());
        dialogBinding.etStudentFaculty.setText(student.getFaculty());

        setupDatePicker(dialogBinding);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogBinding.getRoot())
                .create();

        dialogBinding.btnCancelStudent.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnSaveStudent.setOnClickListener(v -> {
            String name = dialogBinding.etStudentName.getText() != null ? VietnameseTextInputFormatter.capitalizeWords(dialogBinding.etStudentName.getText().toString().trim()) : "";
            String dob = dialogBinding.etStudentDob.getText() != null ? dialogBinding.etStudentDob.getText().toString().trim() : "";
            String className = dialogBinding.etStudentClass.getText() != null ? dialogBinding.etStudentClass.getText().toString().trim() : "";
            String faculty = dialogBinding.etStudentFaculty.getText() != null ? VietnameseTextInputFormatter.capitalizeWords(dialogBinding.etStudentFaculty.getText().toString().trim()) : "";
            String email = dialogBinding.etStudentEmail != null && dialogBinding.etStudentEmail.getText() != null ? dialogBinding.etStudentEmail.getText().toString().trim() : "";

            String validationError = StudentDataValidation.validateStudent(
                    student.getStudentId(), name, dob, className, faculty);
            if (validationError != null) {
                Snackbar.make(dialogBinding.getRoot(), validationError, Snackbar.LENGTH_LONG).show();
                return;
            }

            if (!email.isEmpty() && !ValidationUtils.isValidEmail(email)) {
                dialogBinding.tilStudentEmail.setError(getString(R.string.error_invalid_email));
                return;
            } else {
                dialogBinding.tilStudentEmail.setError(null);
            }

            student.setName(name);
            student.setDateOfBirth(dob);
            student.setClassName(className);
            student.setFaculty(faculty);

            viewModel.updateStudent(student);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void setupDatePicker(DialogStudentAddEditBinding dialogBinding) {
        View.OnClickListener clickListener = v -> {
            Calendar cal = Calendar.getInstance();
            DatePickerDialog dpd = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                String formatted = String.format(Locale.ROOT, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                dialogBinding.etStudentDob.setText(formatted);
                dialogBinding.tilStudentDob.setError(null);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        };

        dialogBinding.etStudentDob.setFocusable(false);
        dialogBinding.etStudentDob.setOnClickListener(clickListener);
        dialogBinding.tilStudentDob.setEndIconOnClickListener(clickListener);
    }

    private void showDeleteConfirmDialog(Student student) {
        if (!canManageStudents) return;
        int certCount = student.getCertificateCount();
        String warningMsg = getString(R.string.dialog_delete_student_warning_format, student.getName(), certCount);
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.dialog_delete_student_title)
                .setMessage(warningMsg)
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(R.string.btn_confirm_delete, (dialog, which) -> viewModel.deleteStudent(student.getStudentId()))
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    private void exportCsv() {
        List<Student> students = viewModel.getAllCachedStudents();
        if (students == null || students.isEmpty()) {
            Toast.makeText(requireContext(), "Không có dữ liệu sinh viên để xuất.", Toast.LENGTH_SHORT).show();
            return;
        }
        String csvData = StudentCsvHelper.exportToCsv(students);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, csvData);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getString(R.string.title_import_export)));
        Toast.makeText(requireContext(), getString(R.string.csv_exported_toast, students.size()), Toast.LENGTH_SHORT).show();
    }

    private boolean currentUserCanManageStudents() {
        AppUser user = AuthRepositoryProvider.getRepository().getCurrentUser();
        return user != null && user.isNormal() && (user.isAdmin() || user.isManager());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
