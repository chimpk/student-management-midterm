package com.example.studentmgmt.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.studentmgmt.R;
import com.example.studentmgmt.data.AuthRepositoryProvider;
import com.example.studentmgmt.data.StudentRepository;
import com.example.studentmgmt.data.StudentRepositoryProvider;
import com.example.studentmgmt.databinding.FragmentDashboardBinding;
import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.model.Student;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Shared Dashboard Fragment covering:
 * - S04 (Admin Light) / S28 (Admin Dark)
 * - S37 (Manager Light) / S38 (Manager Dark)
 * - S09 (Employee Light) / S39 (Employee Dark)
 *
 * Dynamically switches cards and stats based on user role (RBAC).
 */
public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private AppUser currentUser;
    private StudentRepository.ListenerRegistrationHandle listenerHandle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentUser = AuthRepositoryProvider.getRepository().getCurrentUser();
        if (currentUser == null) return;

        setupHeader();
        setupRbacVisibility();
        setupCardListeners();
        loadStudentStats();
    }

    private void setupHeader() {
        String displayName = currentUser.getName();
        if (displayName == null || displayName.trim().isEmpty()) {
            displayName = currentUser.getUsername();
        }
        binding.tvGreeting.setText(getString(R.string.greeting_format, displayName));

        if (currentUser.isAdmin()) {
            binding.chipRole.setText(R.string.role_label_admin);
            binding.chipRole.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_role_admin));
        } else if (currentUser.isManager()) {
            binding.chipRole.setText(R.string.role_label_manager);
            binding.chipRole.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_role_manager));
        } else {
            binding.chipRole.setText(R.string.role_label_employee);
            binding.chipRole.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_role_employee));
        }
    }

    private void setupRbacVisibility() {
        if (currentUser.isAdmin()) {
            // S04 / S28
            binding.cardStatsBanner.setVisibility(View.VISIBLE);
            binding.cardEmployeeSearch.setVisibility(View.GONE);
            binding.cardStudents.setVisibility(View.VISIBLE);
            binding.cardUsers.setVisibility(View.VISIBLE);
            binding.cardImportExport.setVisibility(View.VISIBLE);
            binding.cardProfile.setVisibility(View.VISIBLE);
        } else if (currentUser.isManager()) {
            // S37 / S38: Manager has Students, Import/Export, Profile; Users is GONE
            binding.cardStatsBanner.setVisibility(View.VISIBLE);
            binding.cardEmployeeSearch.setVisibility(View.GONE);
            binding.cardStudents.setVisibility(View.VISIBLE);
            binding.cardUsers.setVisibility(View.GONE);
            binding.cardImportExport.setVisibility(View.VISIBLE);
            binding.cardProfile.setVisibility(View.VISIBLE);
        } else {
            // S09 / S39: Employee has search card + Profile; Users and Import/Export are GONE
            binding.cardStatsBanner.setVisibility(View.GONE);
            binding.cardEmployeeSearch.setVisibility(View.VISIBLE);
            binding.cardStudents.setVisibility(View.VISIBLE);
            binding.cardUsers.setVisibility(View.GONE);
            binding.cardImportExport.setVisibility(View.GONE);
            binding.cardProfile.setVisibility(View.VISIBLE);
        }
    }

    private void setupCardListeners() {
        View.OnClickListener toStudents = v ->
                Navigation.findNavController(v).navigate(R.id.nav_student_list);

        binding.cardStudents.setOnClickListener(toStudents);
        binding.btnEmployeeViewStudents.setOnClickListener(toStudents);

        binding.cardUsers.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.nav_user_management));

        binding.cardImportExport.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.nav_import_export));

        binding.cardProfile.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.nav_profile));

        binding.ivHeaderAvatar.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.nav_profile));
    }

    private void loadStudentStats() {
        StudentRepository repo = StudentRepositoryProvider.getInstance();
        listenerHandle = repo.listenStudents(new StudentRepository.StudentListCallback() {
            @Override
            public void onSuccess(List<Student> students) {
                if (!isAdded() || students == null || binding == null) return;
                int count = students.size();
                Set<String> faculties = new HashSet<>();
                Set<String> classes = new HashSet<>();

                for (Student s : students) {
                    if (s.getFaculty() != null && !s.getFaculty().trim().isEmpty()) {
                        faculties.add(s.getFaculty().trim());
                    }
                    if (s.getClassName() != null && !s.getClassName().trim().isEmpty()) {
                        classes.add(s.getClassName().trim());
                    }
                }
                binding.tvStatsSummary.setText(getString(R.string.stats_summary_format,
                        count, faculties.size(), classes.size()));
            }

            @Override
            public void onError(String errorMessage) {
                if (!isAdded() || binding == null) return;
                binding.tvStatsSummary.setText(R.string.stats_error);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listenerHandle != null) {
            listenerHandle.remove();
            listenerHandle = null;
        }
        binding = null;
    }
}
