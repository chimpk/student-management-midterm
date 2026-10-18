package com.example.studentmgmt.ui.history;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmgmt.databinding.ItemLoginHistoryBinding;
import com.example.studentmgmt.model.LoginRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for displaying login history records.
 */
public class LoginHistoryAdapter extends RecyclerView.Adapter<LoginHistoryAdapter.ViewHolder> {

    private final List<LoginRecord> records = new ArrayList<>();

    public void setRecords(List<LoginRecord> newRecords) {
        records.clear();
        if (newRecords != null) {
            records.addAll(newRecords);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLoginHistoryBinding binding = ItemLoginHistoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(records.get(position));
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemLoginHistoryBinding binding;

        ViewHolder(ItemLoginHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(LoginRecord record) {
            binding.tvDeviceName.setText(itemView.getContext().getString(
                    com.example.studentmgmt.R.string.history_device_format,
                    record.getDevice() != null ? record.getDevice() : ""
            ));
            binding.tvLoginTime.setText(itemView.getContext().getString(
                    com.example.studentmgmt.R.string.history_time_format,
                    record.getFormattedDateTime() != null ? record.getFormattedDateTime() : ""
            ));
        }
    }
}
