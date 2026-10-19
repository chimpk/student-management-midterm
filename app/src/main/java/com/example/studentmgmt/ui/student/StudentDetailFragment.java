package com.example.studentmgmt.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentmgmt.R;
import com.example.studentmgmt.data.AuthRepositoryProvider;
import com.example.studentmgmt.databinding.DialogCertificateAddBinding;
import com.example.studentmgmt.databinding.FragmentStudentDetailBinding;
import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.model.Certificate;
import com.example.studentmgmt.utils.StudentDataValidation;

/**
 * TODO(member-2): Student Detail and Certificate management dialog/fragment for Member 2.
 */
public class StudentDetailFragment extends DialogFragment implements CertificateAdapter.OnCertificateDeleteListener {

    private static final String ARG_STUDENT_ID = "arg_student_id";

    private FragmentStudentDetailBinding binding;
    private StudentDetailViewModel viewModel;
    private CertificateAdapter certificateAdapter;
    private String studentId;

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
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(StudentDetailViewModel.class);

        setupRecyclerView();
        observeViewModel();

        if (studentId != null) {
            viewModel.loadStudent(studentId);
        }

        binding.btnAddCertificate.setVisibility(canManageData() ? View.VISIBLE : View.GONE);
        binding.btnAddCertificate.setOnClickListener(v -> {
            if (canManageData()) showCertificateDialog(null); else showUnauthorized();
        });
    }

    private void setupRecyclerView() {
        certificateAdapter = new CertificateAdapter(this, canManageData());
        binding.rvCertificates.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCertificates.setAdapter(certificateAdapter);
    }

    private void observeViewModel() {
        viewModel.getStudentData().observe(getViewLifecycleOwner(), student -> {
            if (student != null) {
                binding.tvDetailName.setText(student.getName());
                binding.tvDetailId.setText(getString(R.string.student_id_format, student.getStudentId()));
                binding.tvDetailDob.setText(getString(R.string.student_dob_format, student.getDateOfBirth()));
                binding.tvDetailClass.setText(getString(R.string.student_class_format, student.getClassName()));
                binding.tvDetailFaculty.setText(getString(R.string.student_faculty_format, student.getFaculty()));
            }
        });

        viewModel.getCertificateListState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            if (state.isLoading()) {
                binding.progressLoadingCertificates.setVisibility(View.VISIBLE);
                binding.rvCertificates.setVisibility(View.GONE);
                binding.tvEmptyCertificates.setVisibility(View.GONE);
            } else if (state.isContent()) {
                binding.progressLoadingCertificates.setVisibility(View.GONE);
                binding.rvCertificates.setVisibility(View.VISIBLE);
                binding.tvEmptyCertificates.setVisibility(View.GONE);
                certificateAdapter.setCertificates(state.getData());
            } else if (state.isEmpty()) {
                binding.progressLoadingCertificates.setVisibility(View.GONE);
                binding.rvCertificates.setVisibility(View.GONE);
                binding.tvEmptyCertificates.setVisibility(View.VISIBLE);
            } else if (state.isError()) {
                binding.progressLoadingCertificates.setVisibility(View.GONE);
                binding.rvCertificates.setVisibility(View.GONE);
                binding.tvEmptyCertificates.setVisibility(View.VISIBLE);
                binding.tvEmptyCertificates.setText(state.getErrorMessage());
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

    private void showAddCertificateDialog() {
        showCertificateDialog(null);
    }

    private void showCertificateDialog(@Nullable Certificate existing) {
        if (!canManageData()) { showUnauthorized(); return; }
        DialogCertificateAddBinding dialogBinding = DialogCertificateAddBinding.inflate(getLayoutInflater());

        if (existing != null) {
            dialogBinding.etCertName.setText(existing.getCertName());
            dialogBinding.etCertIssuer.setText(existing.getIssuedBy());
            dialogBinding.etCertDate.setText(existing.getIssueDate());
        }

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(existing == null ? R.string.dialog_add_cert_title : R.string.dialog_edit_cert_title)
                .setView(dialogBinding.getRoot())
                .setPositiveButton(R.string.btn_save, null)
                .setNegativeButton(R.string.btn_cancel, null)
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = dialogBinding.etCertName.getText() != null ? dialogBinding.etCertName.getText().toString().trim() : "";
            String issuer = dialogBinding.etCertIssuer.getText() != null ? dialogBinding.etCertIssuer.getText().toString().trim() : "";
            String date = dialogBinding.etCertDate.getText() != null ? dialogBinding.etCertDate.getText().toString().trim() : "";

            dialogBinding.tilCertName.setError(null);
            dialogBinding.tilCertDate.setError(null);
            dialogBinding.tilCertIssuer.setError(null);
            String validationError = StudentDataValidation.validateCertificate(name, date, issuer);
            if (validationError != null) {
                if (validationError.startsWith("Tên")) dialogBinding.tilCertName.setError(validationError);
                else if (validationError.startsWith("Ngày")) dialogBinding.tilCertDate.setError(validationError);
                else dialogBinding.tilCertIssuer.setError(validationError);
                return;
            }

            Certificate value = new Certificate(existing == null ? null : existing.getCertId(), name, date, issuer);
            if (existing == null) viewModel.addCertificate(value); else viewModel.updateCertificate(value);
            dialog.dismiss();
        });
    }

    @Override
    public void onEditCertificate(Certificate certificate) {
        showCertificateDialog(certificate);
    }

    @Override
    public void onDeleteCertificate(Certificate certificate) {
        if (!canManageData()) { showUnauthorized(); return; }
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.btn_delete)
                .setMessage(getString(R.string.confirm_delete_cert_format, certificate.getCertName()))
                .setPositiveButton(R.string.btn_delete, (dialog, which) -> viewModel.deleteCertificate(certificate.getCertId()))
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
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
