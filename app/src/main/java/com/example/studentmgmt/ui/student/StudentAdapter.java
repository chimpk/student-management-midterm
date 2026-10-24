package com.example.studentmgmt.ui.student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmgmt.R;
import com.example.studentmgmt.databinding.ItemStudentBinding;
import com.example.studentmgmt.model.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * Student list adapter conforming to S07/S12 card design.
 */
public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    public interface OnStudentClickListener {
        void onStudentClick(Student student);

        void onStudentEdit(Student student);

        void onStudentDelete(Student student);
    }

    private final List<Student> students = new ArrayList<>();
    private final OnStudentClickListener listener;
    private boolean isReadOnly = false;
    private boolean canManageStudents = true;

    public StudentAdapter(OnStudentClickListener listener) {
        this.listener = listener;
    }

    public void setReadOnly(boolean readOnly) {
        this.isReadOnly = readOnly;
        this.canManageStudents = !readOnly;
        notifyDataSetChanged();
    }

    public void setStudents(List<Student> newStudents) {
        students.clear();
        if (newStudents != null) {
            students.addAll(newStudents);
        }
        notifyDataSetChanged();
    }

    public void setCanManageStudents(boolean canManageStudents) {
        this.canManageStudents = canManageStudents;
        this.isReadOnly = !canManageStudents;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStudentBinding binding = ItemStudentBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new StudentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        holder.bind(students.get(position));
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {

        private final ItemStudentBinding binding;

        public StudentViewHolder(ItemStudentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Student student) {
            String name = student.getName() != null ? student.getName() : "";
            binding.tvStudentName.setText(name);

            String initial = !name.isEmpty() ? name.substring(0, 1).toUpperCase() : "S";
            binding.tvAvatarLetter.setText(initial);

            String faculty = student.getFaculty() != null ? student.getFaculty() : "";
            String className = student.getClassName() != null ? student.getClassName() : "";
            String subtitle = student.getStudentId() + (faculty.isEmpty() ? "" : " • " + faculty) + (className.isEmpty() ? "" : " • " + className);
            binding.tvStudentId.setText(subtitle);

            int certCount = student.getCertificateCount();
            if (certCount > 0) {
                binding.chipCertCount.setVisibility(View.VISIBLE);
                binding.chipCertCount.setText(binding.getRoot().getContext().getString(R.string.cert_count_format, certCount));
            } else {
                binding.chipCertCount.setVisibility(View.GONE);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onStudentClick(student);
            });

            if (isReadOnly) {
                binding.btnStudentEdit.setVisibility(View.GONE);
                binding.btnStudentDelete.setVisibility(View.GONE);
            } else {
                binding.btnStudentEdit.setVisibility(View.VISIBLE);
                binding.btnStudentDelete.setVisibility(View.VISIBLE);
                binding.btnStudentEdit.setOnClickListener(v -> {
                    if (listener != null) listener.onStudentEdit(student);
                });
                binding.btnStudentDelete.setOnClickListener(v -> {
                    if (listener != null) listener.onStudentDelete(student);
                });
            }
        }
    }
}
