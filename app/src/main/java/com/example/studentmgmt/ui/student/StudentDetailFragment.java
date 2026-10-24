package com.example.studentmgmt.ui.student;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentmgmt.R;
import com.example.studentmgmt.data.AuthRepositoryProvider;
import com.example.studentmgmt.databinding.DialogCertificateAddBinding;
import com.example.studentmgmt.databinding.DialogStudentAddEditBinding;
import com.example.studentmgmt.databinding.FragmentStudentDetailBinding;
import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.model.Certificate;
import com.example.studentmgmt.model.Student;
import com.example.studentmgmt.model.UiState;
import com.example.studentmgmt.utils.ValidationUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Student Detail and Certificate management screen conforming to S08, S15, S16, S30, S46-S54.
 * Accommodates Admin/Manager CRUD and Employee read-only mode.
 */
public class StudentDetailFragment extends DialogFragment implements CertificateAdapter.OnCertificateActionListener {

    private static final String ARG_STUDENT_ID = "arg_student_id";

    private FragmentStudentDetailBinding binding;
    private StudentDetailViewModel viewModel;
    private CertificateAdapter certificateAdapter;
    private String studentId;
    private Student currentStudent;
    private boolean isEmployee = false;
    private int cachedCertificateCount = 0;

    public static StudentDetailFragment newInstance(String studentId) {
        StudentDetailFragment fragment = new StudentDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STUDENT_ID, studentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            studentId = getArguments().getString(ARG_STUDENT_ID);
            if (studentId == null) {
                studentId = getArguments().getString("studentId");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStudentDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(StudentDetailViewModel.class);

        AppUser currentUser = AuthRepositoryProvider.getRepository().getCurrentUser();
        isEmployee = currentUser != null && currentUser.isEmployee();

        setupToolbar();
        setupRecyclerView();
        observeViewModel();

        if (studentId != null) {
            viewModel.loadStudent(studentId);
        }

        binding.btnAddCertificate.setOnClickListener(v -> showAddCertificateDialog());
    }

    private void setupToolbar() {
        binding.toolbarStudentDetail.setNavigationOnClickListener(v -> navigateBack(v));

        // 3-dot Menu (S15) - Hidden for Employee
        if (!isEmployee) {
            binding.toolbarStudentDetail.inflateMenu(R.menu.menu_student_detail);
            binding.toolbarStudentDetail.setOnMenuItemClickListener(this::onToolbarMenuItemClick);
        }
    }

    private boolean onToolbarMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_edit_student) {
            if (currentStudent != null) {
                showEditStudentDialog(currentStudent);
            }
            return true;
        } else if (item.getItemId() == R.id.action_delete_student) {
            if (currentStudent != null) {
                showDeleteCascadeConfirmDialog(currentStudent);
            }
            return true;
        }
        return false;
    }

    private void setupRecyclerView() {
        certificateAdapter = new CertificateAdapter(this);
        certificateAdapter.setReadOnly(isEmployee);
        binding.rvCertificates.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCertificates.setAdapter(certificateAdapter);

        if (isEmployee) {
            binding.chipReadOnlyCert.setVisibility(View.VISIBLE);
            binding.btnAddCertificate.setVisibility(View.GONE);
            binding.includeCertEmpty.btnEmptyAdd.setVisibility(View.GONE);
            binding.includeCertEmpty.btnEmptyImport.setVisibility(View.GONE);
        } else {
            binding.chipReadOnlyCert.setVisibility(View.GONE);
            binding.btnAddCertificate.setVisibility(View.VISIBLE);
            binding.includeCertEmpty.btnEmptyAdd.setOnClickListener(v -> showAddCertificateDialog());
            binding.includeCertEmpty.btnEmptyImport.setVisibility(View.GONE);
        }

        binding.includeCertError.btnRetry.setOnClickListener(v -> viewModel.retryCertificates());
    }

    private void observeViewModel() {
        viewModel.getStudentData().observe(getViewLifecycleOwner(), student -> {
            if (student != null) {
                this.currentStudent = student;
                renderStudentInfo(student);
            }
        });

        viewModel.getCertificateListState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            if (state.isLoading()) {
                binding.includeCertLoading.getRoot().setVisibility(View.VISIBLE);
                binding.rvCertificates.setVisibility(View.GONE);
                binding.includeCertEmpty.getRoot().setVisibility(View.GONE);
                binding.includeCertError.getRoot().setVisibility(View.GONE);
            } else if (state.isContent()) {
                binding.includeCertLoading.getRoot().setVisibility(View.GONE);
                binding.rvCertificates.setVisibility(View.VISIBLE);
                binding.includeCertEmpty.getRoot().setVisibility(View.GONE);
                binding.includeCertError.getRoot().setVisibility(View.GONE);

                List<Certificate> certs = state.getData();
                cachedCertificateCount = certs != null ? certs.size() : 0;
                binding.tvCertSectionTitle.setText(getString(R.string.section_certificates_format, cachedCertificateCount));
                certificateAdapter.setCertificates(certs);
            } else if (state.isEmpty()) {
                cachedCertificateCount = 0;
                binding.tvCertSectionTitle.setText(getString(R.string.section_certificates_format, 0));
                binding.includeCertLoading.getRoot().setVisibility(View.GONE);
                binding.rvCertificates.setVisibility(View.GONE);
                binding.includeCertEmpty.getRoot().setVisibility(View.VISIBLE);
                binding.includeCertError.getRoot().setVisibility(View.GONE);

                String name = currentStudent != null ? currentStudent.getName() : "";
                binding.includeCertEmpty.tvEmptyTitle.setText(R.string.certificate_empty_title);
                binding.includeCertEmpty.tvEmptyDesc.setText(getString(R.string.certificate_empty_desc_format, name));
            } else if (state.isError()) {
                binding.includeCertLoading.getRoot().setVisibility(View.GONE);
                binding.rvCertificates.setVisibility(View.GONE);
                binding.includeCertEmpty.getRoot().setVisibility(View.GONE);
                binding.includeCertError.getRoot().setVisibility(View.VISIBLE);

                binding.includeCertError.tvErrorTitle.setText(R.string.certificate_error_title);
                binding.includeCertError.tvErrorDesc.setText(state.getErrorMessage());
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
                Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show();
                viewModel.clearActionMessages();
            }
        });
    }

    private void renderStudentInfo(Student student) {
        String name = student.getName() != null ? student.getName() : "";
        binding.tvDetailName.setText(name);

        String initial = !name.isEmpty() ? name.substring(0, 1).toUpperCase() : "S";
        binding.tvDetailAvatarLetter.setText(initial);

        binding.tvDetailId.setText(getString(R.string.student_id_format, student.getStudentId()));
        binding.tvDetailDob.setText(student.getDateOfBirth());
        binding.tvDetailClass.setText(student.getClassName());
        binding.tvDetailFaculty.setText(student.getFaculty());

        String email = student.getStudentId().toLowerCase(Locale.ROOT) + "@student.tdtu.edu.vn";
        binding.tvDetailEmail.setText(email);
    }

    private void showAddCertificateDialog() {
        DialogCertificateAddBinding dialogBinding = DialogCertificateAddBinding.inflate(getLayoutInflater());
        dialogBinding.tvCertDialogTitle.setText(R.string.title_add_certificate);

        String mssv = currentStudent != null ? currentStudent.getStudentId() : studentId;
        String studentName = currentStudent != null ? currentStudent.getName() : "";
        dialogBinding.tvCertStudentBinding.setText(getString(R.string.certificate_student_binding_format, mssv, studentName));

        setupCertDatePicker(dialogBinding);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogBinding.getRoot())
                .create();

        dialogBinding.btnCancelCert.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnSaveCert.setOnClickListener(v -> {
            String name = dialogBinding.etCertName.getText() != null ? dialogBinding.etCertName.getText().toString().trim() : "";
            String issuer = dialogBinding.etCertIssuer.getText() != null ? dialogBinding.etCertIssuer.getText().toString().trim() : "";
            String date = dialogBinding.etCertDate.getText() != null ? dialogBinding.etCertDate.getText().toString().trim() : "";

            boolean hasError = false;
            if (name.isEmpty()) {
                dialogBinding.tilCertName.setError(getString(R.string.error_field_required));
                hasError = true;
            } else {
                dialogBinding.tilCertName.setError(null);
            }

            if (issuer.isEmpty()) {
                dialogBinding.tilCertIssuer.setError(getString(R.string.error_field_required));
                hasError = true;
            } else {
                dialogBinding.tilCertIssuer.setError(null);
            }

            if (date.isEmpty()) {
                dialogBinding.tilCertDate.setError(getString(R.string.error_field_required));
                hasError = true;
            } else if (isFutureDate(date)) { // S50 future date validation
                dialogBinding.tilCertDate.setError(getString(R.string.error_issue_date_future));
                hasError = true;
            } else {
                dialogBinding.tilCertDate.setError(null);
            }

            if (hasError) return;

            Certificate newCert = new Certificate(null, name, date, issuer);
            viewModel.addCertificate(newCert);
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onEditCertificate(Certificate cert) {
        DialogCertificateAddBinding dialogBinding = DialogCertificateAddBinding.inflate(getLayoutInflater());
        dialogBinding.tvCertDialogTitle.setText(R.string.title_edit_certificate);

        String mssv = currentStudent != null ? currentStudent.getStudentId() : studentId;
        String studentName = currentStudent != null ? currentStudent.getName() : "";
        dialogBinding.tvCertStudentBinding.setText(getString(R.string.certificate_student_binding_format, mssv, studentName));

        dialogBinding.etCertName.setText(cert.getCertName());
        dialogBinding.etCertIssuer.setText(cert.getIssuedBy());
        dialogBinding.etCertDate.setText(cert.getIssueDate());

        setupCertDatePicker(dialogBinding);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogBinding.getRoot())
                .create();

        dialogBinding.btnCancelCert.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnSaveCert.setOnClickListener(v -> {
            String name = dialogBinding.etCertName.getText() != null ? dialogBinding.etCertName.getText().toString().trim() : "";
            String issuer = dialogBinding.etCertIssuer.getText() != null ? dialogBinding.etCertIssuer.getText().toString().trim() : "";
            String date = dialogBinding.etCertDate.getText() != null ? dialogBinding.etCertDate.getText().toString().trim() : "";

            boolean hasError = false;
            if (name.isEmpty()) {
                dialogBinding.tilCertName.setError(getString(R.string.error_field_required));
                hasError = true;
            } else {
                dialogBinding.tilCertName.setError(null);
            }

            if (issuer.isEmpty()) {
                dialogBinding.tilCertIssuer.setError(getString(R.string.error_field_required));
                hasError = true;
            } else {
                dialogBinding.tilCertIssuer.setError(null);
            }

            if (date.isEmpty()) {
                dialogBinding.tilCertDate.setError(getString(R.string.error_field_required));
                hasError = true;
            } else if (isFutureDate(date)) {
                dialogBinding.tilCertDate.setError(getString(R.string.error_issue_date_future));
                hasError = true;
            } else {
                dialogBinding.tilCertDate.setError(null);
            }

            if (hasError) return;

            cert.setCertName(name);
            cert.setIssuedBy(issuer);
            cert.setIssueDate(date);

            viewModel.updateCertificate(cert);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void setupCertDatePicker(DialogCertificateAddBinding dialogBinding) {
        View.OnClickListener clickListener = v -> {
            Calendar cal = Calendar.getInstance();
            DatePickerDialog dpd = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                String formatted = String.format(Locale.ROOT, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                dialogBinding.etCertDate.setText(formatted);
                if (isFutureDate(formatted)) {
                    dialogBinding.tilCertDate.setError(getString(R.string.error_issue_date_future));
                } else {
                    dialogBinding.tilCertDate.setError(null);
                }
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        };

        dialogBinding.etCertDate.setOnClickListener(clickListener);
        dialogBinding.tilCertDate.setEndIconOnClickListener(clickListener);
    }

    private boolean isFutureDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
            Date parsed = sdf.parse(dateStr);
            if (parsed != null) {
                // Remove time part of current date for fair day comparison
                Calendar todayCal = Calendar.getInstance();
                todayCal.set(Calendar.HOUR_OF_DAY, 23);
                todayCal.set(Calendar.MINUTE, 59);
                todayCal.set(Calendar.SECOND, 59);
                return parsed.after(todayCal.getTime());
            }
        } catch (Exception ignored) {}
        return false;
    }

    @Override
    public void onDeleteCertificate(Certificate certificate) {
        String sName = currentStudent != null ? currentStudent.getName() : "";
        String sId = currentStudent != null ? currentStudent.getStudentId() : studentId;
        String desc = getString(R.string.dialog_delete_certificate_desc_format, certificate.getCertName(), sName, sId);

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.dialog_delete_certificate_title)
                .setMessage(desc)
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(R.string.btn_delete, (dialog, which) -> viewModel.deleteCertificate(certificate.getCertId()))
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    private void showEditStudentDialog(Student student) {
        DialogStudentAddEditBinding dialogBinding = DialogStudentAddEditBinding.inflate(getLayoutInflater());
        dialogBinding.tvDialogTitle.setText(R.string.title_edit_student);
        dialogBinding.tvDialogSubtitle.setText(R.string.subtitle_edit_student);

        dialogBinding.etStudentId.setText(student.getStudentId());
        dialogBinding.etStudentId.setEnabled(false);
        dialogBinding.tvMssvImmutableNote.setVisibility(View.VISIBLE);

        dialogBinding.etStudentName.setText(student.getName());
        dialogBinding.etStudentDob.setText(student.getDateOfBirth());
        dialogBinding.etStudentClass.setText(student.getClassName());
        dialogBinding.etStudentFaculty.setText(student.getFaculty());

        View.OnClickListener dateClick = v -> {
            Calendar cal = Calendar.getInstance();
            DatePickerDialog dpd = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                String formatted = String.format(Locale.ROOT, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                dialogBinding.etStudentDob.setText(formatted);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        };
        dialogBinding.etStudentDob.setFocusable(false);
        dialogBinding.etStudentDob.setOnClickListener(dateClick);
        dialogBinding.tilStudentDob.setEndIconOnClickListener(dateClick);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogBinding.getRoot())
                .create();

        dialogBinding.btnCancelStudent.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnSaveStudent.setOnClickListener(v -> {
            String name = dialogBinding.etStudentName.getText() != null ? dialogBinding.etStudentName.getText().toString().trim() : "";
            String dob = dialogBinding.etStudentDob.getText() != null ? dialogBinding.etStudentDob.getText().toString().trim() : "";
            String className = dialogBinding.etStudentClass.getText() != null ? dialogBinding.etStudentClass.getText().toString().trim() : "";
            String faculty = dialogBinding.etStudentFaculty.getText() != null ? dialogBinding.etStudentFaculty.getText().toString().trim() : "";

            if (name.isEmpty()) {
                dialogBinding.tilStudentName.setError(getString(R.string.error_field_required));
                return;
            }

            student.setName(name);
            student.setDateOfBirth(dob);
            student.setClassName(className);
            student.setFaculty(faculty);

            viewModel.updateStudent(student);
            renderStudentInfo(student);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showDeleteCascadeConfirmDialog(Student student) {
        String warningMsg = getString(R.string.dialog_delete_student_warning_format, student.getName(), cachedCertificateCount);

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.dialog_delete_student_title)
                .setMessage(warningMsg)
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(R.string.btn_confirm_delete, (dialog, which) -> {
                    viewModel.deleteStudent(student.getStudentId(), () -> {
                        if (isAdded() && getView() != null) {
                            navigateBack(getView());
                        }
                    });
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    private void navigateBack(View v) {
        if (getDialog() != null) {
            dismiss();
        } else {
            try {
                if (!Navigation.findNavController(v).navigateUp()) {
                    requireActivity().onBackPressed();
                }
            } catch (Exception e) {
                requireActivity().onBackPressed();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
