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
 * TODO(member-2): Student list adapter for Member 2.
 */
public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    public interface OnStudentClickListener {
        void onStudentClick(Student student);
        void onStudentEdit(Student student);
        void onStudentDelete(Student student);
    }

    private final List<Student> students = new ArrayList<>();
    private final OnStudentClickListener listener;
    private boolean canManageStudents;

    public StudentAdapter(OnStudentClickListener listener) {
        this.listener = listener;
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
            binding.tvStudentName.setText(student.getName());
            binding.tvStudentId.setText(binding.getRoot().getContext().getString(R.string.student_id_format, student.getStudentId()));
            binding.tvStudentClass.setText(binding.getRoot().getContext().getString(R.string.student_class_format, student.getClassName()));
            binding.tvStudentFaculty.setText(student.getFaculty());
            binding.chipCertCount.setText(binding.getRoot().getContext().getString(R.string.cert_count_format, student.getCertificateCount()));
            int actionVisibility = canManageStudents ? View.VISIBLE : View.GONE;
            binding.btnStudentEdit.setVisibility(actionVisibility);
            binding.btnStudentDelete.setVisibility(actionVisibility);

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onStudentClick(student);
            });

            binding.btnStudentEdit.setOnClickListener(v -> {
                if (listener != null) listener.onStudentEdit(student);
            });

            binding.btnStudentDelete.setOnClickListener(v -> {
                if (listener != null) listener.onStudentDelete(student);
            });
        }
    }
}
