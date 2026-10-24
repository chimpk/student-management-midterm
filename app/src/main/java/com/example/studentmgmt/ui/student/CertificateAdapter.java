package com.example.studentmgmt.ui.student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmgmt.R;
import com.example.studentmgmt.databinding.ItemCertificateBinding;
import com.example.studentmgmt.model.Certificate;

import java.util.ArrayList;
import java.util.List;

/**
 * Certificate adapter conforming to S46/S47.
 * Supports read-only mode for Employee role, and edit/delete callbacks.
 */
public class CertificateAdapter extends RecyclerView.Adapter<CertificateAdapter.CertificateViewHolder> {

    public interface OnCertificateActionListener {
        void onEditCertificate(Certificate certificate);
        void onDeleteCertificate(Certificate certificate);
    }

    // Retain legacy interface for backward compatibility if any test references it
    public interface OnCertificateDeleteListener {
        void onDeleteCertificate(Certificate certificate);
    }

    private final List<Certificate> certificates = new ArrayList<>();
    private final OnCertificateActionListener actionListener;
    private boolean isReadOnly = false;

    public CertificateAdapter(OnCertificateActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public CertificateAdapter(OnCertificateDeleteListener deleteListener) {
        this.actionListener = new OnCertificateActionListener() {
            @Override
            public void onEditCertificate(Certificate certificate) {}

            @Override
            public void onDeleteCertificate(Certificate certificate) {
                if (deleteListener != null) {
                    deleteListener.onDeleteCertificate(certificate);
                }
            }
        };
    }

    public void setReadOnly(boolean readOnly) {
        this.isReadOnly = readOnly;
        notifyDataSetChanged();
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

            if (isReadOnly) {
                binding.layoutCertActions.setVisibility(View.GONE);
            } else {
                binding.layoutCertActions.setVisibility(View.VISIBLE);
                binding.btnCertEdit.setOnClickListener(v -> {
                    if (actionListener != null) {
                        actionListener.onEditCertificate(cert);
                    }
                });
                binding.btnCertDelete.setOnClickListener(v -> {
                    if (actionListener != null) {
                        actionListener.onDeleteCertificate(cert);
                    }
                });
            }
        }
    }
}
