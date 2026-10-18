package com.example.studentmgmt.ui.user;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmgmt.databinding.ItemUserBinding;
import com.example.studentmgmt.model.AppUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * RecyclerView adapter for displaying the list of AppUsers in User Management.
 * Binds user properties and dispatches lock, edit, and delete user actions.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    public interface OnUserActionListener {
        void onToggleLock(AppUser user);
        void onEditUser(AppUser user);
        void onDeleteUser(AppUser user);
        default void onViewHistory(AppUser user) {}
    }

    private final List<AppUser> users = new ArrayList<>();
    private final AppUser currentUser;
    private final OnUserActionListener listener;

    public UserAdapter(AppUser currentUser, OnUserActionListener listener) {
        this.currentUser = currentUser;
        this.listener = listener;
    }

    public void submitList(List<AppUser> newUsers) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return users.size();
            }

            @Override
            public int getNewListSize() {
                return newUsers != null ? newUsers.size() : 0;
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                if (newUsers == null) return false;
                return Objects.equals(users.get(oldItemPosition).getUid(), newUsers.get(newItemPosition).getUid());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                if (newUsers == null) return false;
                return Objects.equals(users.get(oldItemPosition), newUsers.get(newItemPosition));
            }
        });

        users.clear();
        if (newUsers != null) {
            users.addAll(newUsers);
        }
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding binding = ItemUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        private final ItemUserBinding binding;

        UserViewHolder(ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(AppUser user) {
            binding.tvUserName.setText(user.getName());
            binding.tvUserUsername.setText(itemView.getContext().getString(
                    com.example.studentmgmt.R.string.username_display_format,
                    user.getUsername() != null ? user.getUsername() : ""
            ));
            binding.tvUserDetails.setText(itemView.getContext().getString(
                    com.example.studentmgmt.R.string.user_details_format,
                    user.getAge(),
                    user.getPhone() != null ? user.getPhone() : ""
            ));

            // Role chip
            String roleText = user.getRole();
            if (AppUser.ROLE_ADMIN.equalsIgnoreCase(roleText)) {
                roleText = itemView.getContext().getString(com.example.studentmgmt.R.string.role_admin);
            } else if (AppUser.ROLE_MANAGER.equalsIgnoreCase(roleText)) {
                roleText = itemView.getContext().getString(com.example.studentmgmt.R.string.role_manager);
            } else if (AppUser.ROLE_EMPLOYEE.equalsIgnoreCase(roleText)) {
                roleText = itemView.getContext().getString(com.example.studentmgmt.R.string.role_employee);
            }
            binding.chipUserRole.setText(roleText);

            // Status chip
            boolean isLocked = user.isLocked();
            binding.chipUserStatus.setText(isLocked
                    ? itemView.getContext().getString(com.example.studentmgmt.R.string.status_locked)
                    : itemView.getContext().getString(com.example.studentmgmt.R.string.status_active));

            // Self-action protections: admin cannot lock or delete themselves
            boolean isSelf = currentUser != null && Objects.equals(currentUser.getUid(), user.getUid());

            binding.btnLockUser.setText(isLocked
                    ? itemView.getContext().getString(com.example.studentmgmt.R.string.btn_unlock)
                    : itemView.getContext().getString(com.example.studentmgmt.R.string.btn_lock));
            binding.btnLockUser.setEnabled(!isSelf);
            binding.btnLockUser.setAlpha(isSelf ? 0.5f : 1.0f);
            binding.btnLockUser.setOnClickListener(v -> {
                if (listener != null && !isSelf) {
                    listener.onToggleLock(user);
                }
            });

            binding.btnEditUser.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditUser(user);
                }
            });

            binding.btnDeleteUser.setEnabled(!isSelf);
            binding.btnDeleteUser.setAlpha(isSelf ? 0.5f : 1.0f);
            binding.btnDeleteUser.setOnClickListener(v -> {
                if (listener != null && !isSelf) {
                    listener.onDeleteUser(user);
                }
            });

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewHistory(user);
                }
            });
        }
    }
}
