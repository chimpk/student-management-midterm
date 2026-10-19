package com.example.studentmgmt.ui.student;

import android.os.Bundle;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentmgmt.R;
import com.example.studentmgmt.data.AuthRepositoryProvider;
import com.example.studentmgmt.data.CertificateRepository;
import com.example.studentmgmt.data.CertificateRepositoryProvider;
import com.example.studentmgmt.data.StudentRepository;
import com.example.studentmgmt.data.StudentRepositoryProvider;
import com.example.studentmgmt.databinding.DialogStudentAddEditBinding;
import com.example.studentmgmt.databinding.FragmentStudentListBinding;
import com.example.studentmgmt.model.Student;
import com.example.studentmgmt.model.UiState;
import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.utils.Member2CsvHelper;
import com.example.studentmgmt.utils.StudentDataValidation;
import com.example.studentmgmt.utils.StudentSearchSortUtils.SortOption;
import com.google.android.material.snackbar.Snackbar;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TODO(member-2): Student list screen for Member 2.
 * Supports realtime list, search, CSV import/export, and CRUD.
 */
public class StudentListFragment extends Fragment implements StudentAdapter.OnStudentClickListener {

    private FragmentStudentListBinding binding;
    private StudentListViewModel viewModel;
    private StudentAdapter adapter;
    private ActivityResultLauncher<String[]> openCsvLauncher;
    private ActivityResultLauncher<String> createCsvLauncher;
    private boolean importCertificates;
    private boolean exportCertificates;
    private List<Student> studentsPendingExport;
    private List<CertificateRepository.ExportRow> certificatesPendingExport;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openCsvLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), this::onCsvSelected);
        createCsvLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument("text/csv"), this::onCsvDestinationSelected);
    }

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

        setupRecyclerView();
        setupSearchAndActions();
        observeViewModel();
    }

    private void setupRecyclerView() {
        adapter = new StudentAdapter(this, canManageData());
        binding.rvStudents.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvStudents.setAdapter(adapter);

        binding.btnRetryStudents.setOnClickListener(v -> viewModel.retry());
    }

    private void setupSearchAndActions() {
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

        boolean canManage = canManageData();
        binding.fabAddStudent.setVisibility(canManage ? View.VISIBLE : View.GONE);
        binding.btnImportCsv.setVisibility(canManage ? View.VISIBLE : View.GONE);
        binding.btnExportCsv.setVisibility(canManage ? View.VISIBLE : View.GONE);
        binding.btnSortStudent.setOnClickListener(v -> showSortOptions());
        binding.fabAddStudent.setOnClickListener(v -> {
            if (canManageData()) showAddStudentDialog(); else showUnauthorized();
        });

        binding.btnExportCsv.setOnClickListener(v -> chooseExportType());
        binding.btnImportCsv.setOnClickListener(v -> chooseImportType());
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
        if (canManageData()) showEditStudentDialog(student); else showUnauthorized();
    }

    @Override
    public void onStudentDelete(Student student) {
        if (canManageData()) showDeleteConfirmDialog(student); else showUnauthorized();
    }

    private void showAddStudentDialog() {
        if (!canManageData()) { showUnauthorized(); return; }
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
            String name = dialogBinding.etStudentName.getText() != null ? dialogBinding.etStudentName.getText().toString().trim() : "";
            String dob = dialogBinding.etStudentDob.getText() != null ? dialogBinding.etStudentDob.getText().toString().trim() : "";
            String className = dialogBinding.etStudentClass.getText() != null ? dialogBinding.etStudentClass.getText().toString().trim() : "";
            String faculty = dialogBinding.etStudentFaculty.getText() != null ? dialogBinding.etStudentFaculty.getText().toString().trim() : "";

            if (!validateStudentInput(dialogBinding, id, name, dob, className, faculty)) return;

            Student newStudent = new Student(id, name, dob, className, faculty);
            viewModel.addStudent(newStudent);
            dialog.dismiss();
        });
    }

    private void showEditStudentDialog(Student student) {
        if (!canManageData()) { showUnauthorized(); return; }
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
            String name = dialogBinding.etStudentName.getText() != null ? dialogBinding.etStudentName.getText().toString().trim() : "";
            String dob = dialogBinding.etStudentDob.getText() != null ? dialogBinding.etStudentDob.getText().toString().trim() : "";
            String className = dialogBinding.etStudentClass.getText() != null ? dialogBinding.etStudentClass.getText().toString().trim() : "";
            String faculty = dialogBinding.etStudentFaculty.getText() != null ? dialogBinding.etStudentFaculty.getText().toString().trim() : "";

            if (!validateStudentInput(dialogBinding, student.getStudentId(), name, dob, className, faculty)) return;

            student.setName(name);
            student.setDateOfBirth(dob);
            student.setClassName(className);
            student.setFaculty(faculty);

            viewModel.updateStudent(student);
            dialog.dismiss();
        });
    }

    private void showDeleteConfirmDialog(Student student) {
        if (!canManageData()) { showUnauthorized(); return; }
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

    private boolean validateStudentInput(DialogStudentAddEditBinding dialogBinding,
                                         String id, String name, String dob,
                                         String className, String faculty) {
        dialogBinding.tilStudentId.setError(null);
        dialogBinding.tilStudentName.setError(null);
        dialogBinding.tilStudentDob.setError(null);
        dialogBinding.tilStudentClass.setError(null);
        dialogBinding.tilStudentFaculty.setError(null);
        String error = StudentDataValidation.validateStudent(id, name, dob, className, faculty);
        if (error == null) return true;
        if (error.startsWith("MSSV")) dialogBinding.tilStudentId.setError(error);
        else if (error.startsWith("Họ tên")) dialogBinding.tilStudentName.setError(error);
        else if (error.startsWith("Ngày sinh")) dialogBinding.tilStudentDob.setError(error);
        else if (error.startsWith("Mã lớp")) dialogBinding.tilStudentClass.setError(error);
        else dialogBinding.tilStudentFaculty.setError(error);
        return false;
    }

    private void showSortOptions() {
        String[] options = {
                getString(R.string.sort_student_id_asc), getString(R.string.sort_student_name_asc),
                getString(R.string.sort_student_name_desc), getString(R.string.sort_student_dob_asc),
                getString(R.string.sort_student_dob_desc), getString(R.string.sort_student_class_asc),
                getString(R.string.sort_student_class_desc)
        };
        SortOption[] values = SortOption.values();
        int selected = viewModel.getSortOption().ordinal();
        new AlertDialog.Builder(requireContext()).setTitle(R.string.sort_student_title)
                .setSingleChoiceItems(options, selected, (dialog, which) -> {
                    viewModel.setSortOption(values[which]);
                    dialog.dismiss();
                }).setNegativeButton(R.string.btn_cancel, null).show();
    }

    private void chooseImportType() {
        if (!canManageData()) { showUnauthorized(); return; }
        new AlertDialog.Builder(requireContext()).setTitle(R.string.csv_choose_import_title)
                .setItems(new String[]{getString(R.string.csv_students), getString(R.string.csv_certificates)},
                        (dialog, which) -> {
                            importCertificates = which == 1;
                            openCsvLauncher.launch(new String[]{"text/*", "application/*"});
                        }).show();
    }

    private void chooseExportType() {
        if (!canManageData()) { showUnauthorized(); return; }
        new AlertDialog.Builder(requireContext()).setTitle(R.string.csv_choose_export_title)
                .setItems(new String[]{getString(R.string.csv_students), getString(R.string.csv_certificates)},
                        (dialog, which) -> {
                            exportCertificates = which == 1;
                            if (!exportCertificates) {
                                studentsPendingExport = viewModel.getAllCachedStudents();
                                createCsvLauncher.launch(getString(R.string.csv_export_students_filename));
                                return;
                            }
                            List<String> studentIds = new ArrayList<>();
                            for (Student student : viewModel.getAllCachedStudents()) studentIds.add(student.getStudentId());
                            CertificateRepositoryProvider.getInstance().getCertificatesForStudents(studentIds,
                                    new CertificateRepository.ExportCallback() {
                                        @Override public void onSuccess(List<CertificateRepository.ExportRow> rows) {
                                            certificatesPendingExport = rows;
                                            if (isAdded()) createCsvLauncher.launch(getString(R.string.csv_export_certificates_filename));
                                        }
                                        @Override public void onError(String errorMessage) { showCsvError(errorMessage); }
                                    });
                        }).show();
    }

    private void onCsvSelected(Uri uri) {
        if (uri == null || !isAdded()) return;
        if (!canManageData()) { showUnauthorized(); return; }
        try (InputStream stream = requireContext().getContentResolver().openInputStream(uri)) {
            if (stream == null) throw new IOException("Không mở được tệp đã chọn.");
            InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            if (importCertificates) importCertificates(reader); else importStudents(reader);
        } catch (SecurityException | IOException error) {
            showCsvError("Không thể đọc tệp CSV: " + error.getLocalizedMessage());
        }
    }

    private void importStudents(InputStreamReader reader) throws IOException {
        Set<String> existingIds = new HashSet<>();
        for (Student student : viewModel.getAllCachedStudents()) existingIds.add(student.getStudentId());
        Member2CsvHelper.StudentResult parsed = Member2CsvHelper.parseStudents(reader, existingIds, LocalDate.now());
        if (!parsed.validHeader) { showCsvError(getString(R.string.csv_import_header_error)); return; }
        List<String> errors = formatCsvErrors(parsed.errors);
        List<StudentRepository.ImportItem> items = new ArrayList<>();
        for (Member2CsvHelper.StudentRow row : parsed.rows) items.add(new StudentRepository.ImportItem(row.dataRow, row.student));
        StudentRepositoryProvider.getInstance().importStudents(items, new StudentRepository.ImportCallback() {
            @Override public void onComplete(int importedCount, List<StudentRepository.ImportError> repositoryErrors) {
                for (StudentRepository.ImportError error : repositoryErrors) {
                    errors.add("Dòng " + error.dataRow + " (" + error.studentId + "): " + error.message);
                }
                showImportSummary(importedCount, errors);
            }
            @Override public void onError(String errorMessage) { showCsvError(errorMessage); }
        });
    }

    private void importCertificates(InputStreamReader reader) throws IOException {
        Set<String> knownIds = new HashSet<>();
        for (Student student : viewModel.getAllCachedStudents()) knownIds.add(student.getStudentId());
        Member2CsvHelper.CertificateResult parsed = Member2CsvHelper.parseCertificates(reader, knownIds, LocalDate.now());
        if (!parsed.validHeader) { showCsvError(getString(R.string.csv_import_header_error)); return; }
        List<String> errors = formatCsvErrors(parsed.errors);
        List<CertificateRepository.ImportItem> items = new ArrayList<>();
        for (Member2CsvHelper.CertificateRow row : parsed.certificates) {
            items.add(new CertificateRepository.ImportItem(row.dataRow, row.studentId, row.certificate));
        }
        CertificateRepositoryProvider.getInstance().importCertificates(items,
                new CertificateRepository.ImportCallback() {
                    @Override public void onComplete(int importedCount, List<CertificateRepository.ImportError> repositoryErrors) {
                        for (CertificateRepository.ImportError error : repositoryErrors) {
                            errors.add("Dòng " + error.dataRow + " (" + error.studentId + "): " + error.message);
                        }
                        showImportSummary(importedCount, errors);
                    }
                    @Override public void onError(String errorMessage) { showCsvError(errorMessage); }
                });
    }

    private void onCsvDestinationSelected(Uri uri) {
        if (uri == null || !isAdded()) return;
        if (!canManageData()) { showUnauthorized(); return; }
        try (OutputStream stream = requireContext().getContentResolver().openOutputStream(uri)) {
            if (stream == null) throw new IOException("Không mở được tệp đích.");
            OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
            if (exportCertificates) {
                List<Member2CsvHelper.CertificateRow> rows = new ArrayList<>();
                if (certificatesPendingExport != null) for (CertificateRepository.ExportRow row : certificatesPendingExport) {
                    rows.add(new Member2CsvHelper.CertificateRow(0, row.studentId, row.certificate));
                }
                Member2CsvHelper.writeCertificates(writer, rows);
            } else {
                Member2CsvHelper.writeStudents(writer, studentsPendingExport);
            }
            Toast.makeText(requireContext(), R.string.csv_export_complete, Toast.LENGTH_LONG).show();
        } catch (SecurityException | IOException error) {
            showCsvError("Không thể lưu tệp CSV: " + error.getLocalizedMessage());
        }
    }

    private List<String> formatCsvErrors(List<Member2CsvHelper.CsvError> csvErrors) {
        List<String> result = new ArrayList<>();
        for (Member2CsvHelper.CsvError error : csvErrors) {
            result.add("Dòng " + error.dataRow + (error.identifier.isEmpty() ? "" : " (" + error.identifier + ")")
                    + ": " + error.message);
        }
        return result;
    }

    private void showImportSummary(int importedCount, List<String> errors) {
        if (!isAdded()) return;
        StringBuilder message = new StringBuilder(getString(R.string.csv_import_summary, importedCount, errors.size()));
        int shown = Math.min(errors.size(), 12);
        for (int i = 0; i < shown; i++) message.append("\n\n• ").append(errors.get(i));
        if (errors.size() > shown) message.append("\n\n… và ").append(errors.size() - shown).append(" lỗi khác.");
        new AlertDialog.Builder(requireContext()).setTitle(R.string.csv_import_result_title)
                .setMessage(message.toString()).setPositiveButton(R.string.btn_close, null).show();
    }

    private void showCsvError(String message) {
        if (isAdded()) Snackbar.make(binding != null ? binding.getRoot() : requireActivity().findViewById(android.R.id.content),
                message, Snackbar.LENGTH_LONG).show();
    }

    private boolean canManageData() {
        AppUser user = AuthRepositoryProvider.getRepository().getCurrentUser();
        return user != null && user.isNormal() && (user.isAdmin() || user.isManager());
    }

    private void showUnauthorized() {
        if (isAdded()) Toast.makeText(requireContext(), R.string.student_write_forbidden, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
