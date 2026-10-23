package com.example.studentmgmt.ui.student;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentmgmt.R;
import com.example.studentmgmt.data.AuthRepositoryProvider;
import com.example.studentmgmt.databinding.DialogStudentAddEditBinding;
import com.example.studentmgmt.databinding.FragmentStudentListBinding;
import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.model.Student;
import com.example.studentmgmt.model.UiState;
import com.example.studentmgmt.utils.StudentCsvHelper;
import com.example.studentmgmt.utils.StudentDataValidation;
import com.example.studentmgmt.utils.VietnameseTextInputFormatter;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;

/**
 * TODO(member-2): Student list screen for Member 2.
 * Supports realtime list, search, CSV import/export, and CRUD.
 */
public class StudentListFragment extends Fragment implements StudentAdapter.OnStudentClickListener {

    private FragmentStudentListBinding binding;
    private StudentListViewModel viewModel;
    private StudentAdapter adapter;
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

        setupRecyclerView();
        setupSearchAndActions();
        observeViewModel();
    }

    private void setupRecyclerView() {
        adapter = new StudentAdapter(this);
        adapter.setCanManageStudents(canManageStudents);
        binding.rvStudents.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvStudents.setAdapter(adapter);

        binding.btnRetryStudents.setOnClickListener(v -> viewModel.retry());
    }

    private void setupSearchAndActions() {
        binding.fabAddStudent.setVisibility(canManageStudents ? View.VISIBLE : View.GONE);
        binding.btnImportCsv.setVisibility(canManageStudents ? View.VISIBLE : View.GONE);

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

        binding.fabAddStudent.setOnClickListener(v -> showAddStudentDialog());

        binding.btnExportCsv.setOnClickListener(v -> exportCsv());
        binding.btnImportCsv.setOnClickListener(v -> importCsvSample());
    }

    private void observeViewModel() {
        viewModel.getStudentListState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            if (state.isLoading()) {
                binding.progressLoadingStudents.setVisibility(View.VISIBLE);
                binding.rvStudents.setVisibility(View.GONE);
                binding.tvEmptyStudents.setVisibility(View.GONE);
                binding.layoutErrorStudents.setVisibility(View.GONE);
            } else if (state.isContent()) {
                binding.progressLoadingStudents.setVisibility(View.GONE);
                binding.rvStudents.setVisibility(View.VISIBLE);
                binding.tvEmptyStudents.setVisibility(View.GONE);
                binding.layoutErrorStudents.setVisibility(View.GONE);
                adapter.setStudents(state.getData());
            } else if (state.isEmpty()) {
                binding.progressLoadingStudents.setVisibility(View.GONE);
                binding.rvStudents.setVisibility(View.GONE);
                binding.tvEmptyStudents.setVisibility(View.VISIBLE);
                binding.layoutErrorStudents.setVisibility(View.GONE);
            } else if (state.isError()) {
                binding.progressLoadingStudents.setVisibility(View.GONE);
                binding.rvStudents.setVisibility(View.GONE);
                binding.tvEmptyStudents.setVisibility(View.GONE);
                binding.layoutErrorStudents.setVisibility(View.VISIBLE);
                binding.tvErrorStudents.setText(state.getErrorMessage());
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
    }

    @Override
    public void onStudentClick(Student student) {
        showStudentDetailDialog(student);
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

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.dialog_add_student_title)
                .setView(dialogBinding.getRoot())
                .setPositiveButton(R.string.btn_save, null)
                .setNegativeButton(R.string.btn_cancel, null)
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String id = dialogBinding.etStudentId.getText() != null ? dialogBinding.etStudentId.getText().toString().trim() : "";
            String name = dialogBinding.etStudentName.getText() != null ? VietnameseTextInputFormatter.capitalizeWords(dialogBinding.etStudentName.getText().toString().trim()) : "";
            String dob = dialogBinding.etStudentDob.getText() != null ? dialogBinding.etStudentDob.getText().toString().trim() : "";
            String className = dialogBinding.etStudentClass.getText() != null ? dialogBinding.etStudentClass.getText().toString().trim() : "";
            String faculty = dialogBinding.etStudentFaculty.getText() != null ? VietnameseTextInputFormatter.capitalizeWords(dialogBinding.etStudentFaculty.getText().toString().trim()) : "";

            String validationError = StudentDataValidation.validateStudent(
                    id, name, dob, className, faculty);
            if (validationError != null) {
                Snackbar.make(dialogBinding.getRoot(), validationError, Snackbar.LENGTH_LONG).show();
                return;
            }

            Student newStudent = new Student(id, name, dob, className, faculty);
            viewModel.addStudent(newStudent);
            dialog.dismiss();
        });
    }

    private void showEditStudentDialog(Student student) {
        if (!canManageStudents) return;
        DialogStudentAddEditBinding dialogBinding = DialogStudentAddEditBinding.inflate(getLayoutInflater());

        dialogBinding.etStudentId.setText(student.getStudentId());
        dialogBinding.etStudentId.setEnabled(false); // ID is immutable primary key
        dialogBinding.etStudentName.setText(student.getName());
        dialogBinding.etStudentDob.setText(student.getDateOfBirth());
        dialogBinding.etStudentClass.setText(student.getClassName());
        dialogBinding.etStudentFaculty.setText(student.getFaculty());

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.dialog_edit_student_title)
                .setView(dialogBinding.getRoot())
                .setPositiveButton(R.string.btn_save, null)
                .setNegativeButton(R.string.btn_cancel, null)
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = dialogBinding.etStudentName.getText() != null ? VietnameseTextInputFormatter.capitalizeWords(dialogBinding.etStudentName.getText().toString().trim()) : "";
            String dob = dialogBinding.etStudentDob.getText() != null ? dialogBinding.etStudentDob.getText().toString().trim() : "";
            String className = dialogBinding.etStudentClass.getText() != null ? dialogBinding.etStudentClass.getText().toString().trim() : "";
            String faculty = dialogBinding.etStudentFaculty.getText() != null ? VietnameseTextInputFormatter.capitalizeWords(dialogBinding.etStudentFaculty.getText().toString().trim()) : "";

            String validationError = StudentDataValidation.validateStudent(
                    student.getStudentId(), name, dob, className, faculty);
            if (validationError != null) {
                Snackbar.make(dialogBinding.getRoot(), validationError, Snackbar.LENGTH_LONG).show();
                return;
            }

            student.setName(name);
            student.setDateOfBirth(dob);
            student.setClassName(className);
            student.setFaculty(faculty);

            viewModel.updateStudent(student);
            dialog.dismiss();
        });
    }

    private void showDeleteConfirmDialog(Student student) {
        if (!canManageStudents) return;
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.btn_delete)
                .setMessage(getString(R.string.confirm_delete_student_format, student.getName(), student.getStudentId()))
                .setPositiveButton(R.string.btn_delete, (dialog, which) -> viewModel.deleteStudent(student.getStudentId()))
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    private void showStudentDetailDialog(Student student) {
        // Creates and shows a dialog containing detail & certificates
        StudentDetailFragment detailFragment = StudentDetailFragment.newInstance(student.getStudentId());
        detailFragment.show(getChildFragmentManager(), "student_detail");
    }

    private void exportCsv() {
        List<Student> students = viewModel.getAllCachedStudents();
        String csvData = StudentCsvHelper.exportToCsv(students);
        Toast.makeText(requireContext(), getString(R.string.csv_exported_toast, students.size()), Toast.LENGTH_LONG).show();
    }

    private void importCsvSample() {
        if (!canManageStudents) return;
        String sampleCsv = "Mã SV,Họ và tên,Ngày sinh,Lớp,Khoa\n" +
                "524H0099,Vũ Quốc Đạt,2003-07-22,24H50301,Công nghệ thông tin\n" +
                "524H0100,Lý Mỹ Linh,2003-10-14,24H50302,Kỹ thuật phần mềm";
        try {
            List<Student> imported = StudentCsvHelper.importFromCsv(sampleCsv);
            for (Student s : imported) {
                viewModel.addStudent(s);
            }
            Toast.makeText(requireContext(), getString(R.string.csv_imported_toast, imported.size()), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Lỗi đọc CSV: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
