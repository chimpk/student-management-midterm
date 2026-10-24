package com.example.studentmgmt.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentmgmt.R;
import com.example.studentmgmt.data.AuthRepositoryProvider;
import com.example.studentmgmt.data.LoginHistoryRepository;
import com.example.studentmgmt.data.LoginHistoryRepositoryProvider;
import com.example.studentmgmt.databinding.FragmentLoginHistoryBinding;
import com.example.studentmgmt.model.AppUser;

/**
 * Screen displaying the realtime list of login history records.
 * Conforms to S11, S34, S75-S77.
 */
public class LoginHistoryFragment extends Fragment {

    public static final String ARG_TARGET_UID = "target_uid";
    public static final String ARG_TARGET_NAME = "target_name";

    private FragmentLoginHistoryBinding binding;
    private LoginHistoryViewModel viewModel;
    private LoginHistoryAdapter adapter;

    public static LoginHistoryFragment newInstance(String targetUid, String targetName) {
        LoginHistoryFragment fragment = new LoginHistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TARGET_UID, targetUid);
        args.putString(ARG_TARGET_NAME, targetName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbarLoginHistory.setNavigationOnClickListener(v -> {
            try {
                Navigation.findNavController(v).navigateUp();
            } catch (Exception e) {
                requireActivity().onBackPressed();
            }
        });

        AppUser sessionUser = AuthRepositoryProvider.getRepository().getCurrentUser();
        String targetUid = null;
        String targetName = null;

        if (getArguments() != null) {
            targetUid = getArguments().getString(ARG_TARGET_UID);
            targetName = getArguments().getString(ARG_TARGET_NAME);
        }

        if (targetUid == null && sessionUser != null) {
            targetUid = sessionUser.getUid();
        }

        final String finalTargetUid = targetUid;
        final LoginHistoryRepository repository = LoginHistoryRepositoryProvider.getRepository();

        ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new LoginHistoryViewModel(repository, sessionUser, finalTargetUid);
            }
        };

        viewModel = new ViewModelProvider(this, factory).get(LoginHistoryViewModel.class);

        // Customize header text
        if (viewModel.isSelfHistory()) {
            binding.tvHistoryTitle.setText(R.string.history_title_self);
            binding.tvHistorySubtitle.setText(R.string.history_subtitle_self);
        } else {
            String name = targetName != null && !targetName.isEmpty() ? targetName : getString(R.string.history_uid_format, targetUid);
            binding.tvHistoryTitle.setText(getString(R.string.history_title_target, name));
            binding.tvHistorySubtitle.setText(R.string.history_subtitle_admin);
        }

        adapter = new LoginHistoryAdapter();
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvHistory.setAdapter(adapter);

        binding.btnRetryHistory.setOnClickListener(v -> viewModel.retry());
        binding.includeErrorState.btnRetry.setOnClickListener(v -> viewModel.retry());

        viewModel.getHistoryState().observe(getViewLifecycleOwner(), state -> {
            if (binding == null || state == null) return;
            switch (state.getStatus()) {
                case LOADING:
                    binding.includeLoadingSkeleton.getRoot().setVisibility(View.VISIBLE);
                    binding.includeEmptyState.getRoot().setVisibility(View.GONE);
                    binding.includeErrorState.getRoot().setVisibility(View.GONE);
                    binding.rvHistory.setVisibility(View.GONE);
                    break;
                case CONTENT:
                    binding.includeLoadingSkeleton.getRoot().setVisibility(View.GONE);
                    binding.includeEmptyState.getRoot().setVisibility(View.GONE);
                    binding.includeErrorState.getRoot().setVisibility(View.GONE);
                    binding.rvHistory.setVisibility(View.VISIBLE);
                    adapter.setRecords(state.getData());
                    break;
                case EMPTY:
                    binding.includeLoadingSkeleton.getRoot().setVisibility(View.GONE);
                    binding.includeEmptyState.getRoot().setVisibility(View.VISIBLE);
                    binding.includeEmptyState.tvEmptyTitle.setText(R.string.login_history_empty_title);
                    binding.includeEmptyState.tvEmptyDesc.setText(R.string.login_history_empty_desc);
                    binding.includeEmptyState.layoutEmptyActions.setVisibility(View.GONE);
                    binding.includeErrorState.getRoot().setVisibility(View.GONE);
                    binding.rvHistory.setVisibility(View.GONE);
                    break;
                case ERROR:
                    binding.includeLoadingSkeleton.getRoot().setVisibility(View.GONE);
                    binding.includeEmptyState.getRoot().setVisibility(View.GONE);
                    binding.includeErrorState.getRoot().setVisibility(View.VISIBLE);
                    binding.includeErrorState.tvErrorTitle.setText(R.string.login_history_error_title);
                    binding.includeErrorState.tvErrorDesc.setText(state.getErrorMessage());
                    binding.rvHistory.setVisibility(View.GONE);
                    break;
            }
        });

        viewModel.startListening();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
