package com.example.studentmgmt.ui.student;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmgmt.R;
import com.example.studentmgmt.databinding.ItemCertificateBinding;
import com.example.studentmgmt.model.Certificate;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO(member-2): Certificate adapter for Member 2.
 */
public class CertificateAdapter extends RecyclerView.Adapter<CertificateAdapter.CertificateViewHolder> {

    public interface OnCertificateDeleteListener {
        void onDeleteCertificate(Certificate certificate);
        void onEditCertificate(Certificate certificate);
    }

    private final List<Certificate> certificates = new ArrayList<>();
    private final OnCertificateDeleteListener deleteListener;
    private final boolean canManageCertificates;

    public CertificateAdapter(OnCertificateDeleteListener deleteListener) {
        this(deleteListener, false);
    }

    public CertificateAdapter(OnCertificateDeleteListener deleteListener, boolean canManageCertificates) {
        this.deleteListener = deleteListener;
        this.canManageCertificates = canManageCertificates;
    }

    public void setCertificates(List<Certificate> newCertificates) {
        certificates.clear();
        if (newCertificates != null) {
            certificates.addAll(newCertificates);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CertificateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCertificateBinding binding = ItemCertificateBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CertificateViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CertificateViewHolder holder, int position) {
        holder.bind(certificates.get(position));
    }

    @Override
    public int getItemCount() {
        return certificates.size();
    }

    class CertificateViewHolder extends RecyclerView.ViewHolder {

        private final ItemCertificateBinding binding;

        public CertificateViewHolder(ItemCertificateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Certificate cert) {
            binding.tvCertName.setText(cert.getCertName());
            binding.tvCertIssuer.setText(binding.getRoot().getContext().getString(R.string.cert_issuer_format, cert.getIssuedBy()));
            binding.tvCertDate.setText(binding.getRoot().getContext().getString(R.string.cert_date_format, cert.getIssueDate()));

            binding.btnCertEdit.setVisibility(canManageCertificates ? android.view.View.VISIBLE : android.view.View.GONE);
            binding.btnCertDelete.setVisibility(canManageCertificates ? android.view.View.VISIBLE : android.view.View.GONE);
            binding.btnCertEdit.setOnClickListener(v -> {
                if (canManageCertificates && deleteListener != null) deleteListener.onEditCertificate(cert);
            });

            binding.btnCertDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteCertificate(cert);
                }
            });
        }
    }
}
