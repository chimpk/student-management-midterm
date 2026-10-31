package com.example.studentmgmt.ui.data;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.studentmgmt.R;
import com.example.studentmgmt.data.AuthRepositoryProvider;
import com.example.studentmgmt.data.CertificateRepository;
import com.example.studentmgmt.data.CertificateRepositoryProvider;
import com.example.studentmgmt.data.StudentRepository;
import com.example.studentmgmt.data.StudentRepositoryProvider;
import com.example.studentmgmt.databinding.FragmentImportExportBinding;
import com.example.studentmgmt.model.AppUser;
import com.example.studentmgmt.model.Certificate;
import com.example.studentmgmt.model.Student;
import com.example.studentmgmt.utils.StudentCsvHelper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ImportExportFragment extends Fragment {

    private FragmentImportExportBinding binding;
    private StudentRepository studentRepository;
    private CertificateRepository certificateRepository;
    private StudentRepository.ListenerRegistrationHandle studentListenerHandle;

    private final List<Student> cachedStudents = new ArrayList<>();
    private final Set<String> knownStudentIds = new HashSet<>();

    // Selected certificate file state
    private Uri selectedCertFileUri;
    private String selectedCertFileName = "certificates.csv";
    private long selectedCertFileSize = 0;

    // Background executor
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // Pickers
    private ActivityResultLauncher<String> studentCsvPicker;
    private ActivityResultLauncher<String> certCsvPicker;
    private ActivityResultLauncher<String> studentExportDocLauncher;
    private ActivityResultLauncher<String> certExportDocLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentImportExportBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        studentRepository = StudentRepositoryProvider.getInstance();
        certificateRepository = CertificateRepositoryProvider.getInstance();

        setupNavigation();
        setupPickers();
        checkPermissions();
        loadExistingStudents();
        setupToggles();
        setupStudentActions();
        setupCertActions();

        // Default: Student + Import
        binding.toggleDataType.check(R.id.btnTypeStudent);
        binding.toggleOperationMode.check(R.id.btnModeImport);
        updateViewVisibility();
    }

    private void setupNavigation() {
        binding.toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void checkPermissions() {
        AppUser user = AuthRepositoryProvider.getRepository().getCurrentUser();
        if (user != null && user.isEmployee()) {
            binding.cardAccessDenied.setVisibility(View.VISIBLE);
            binding.cardStudentImport.setVisibility(View.GONE);
            binding.cardStudentExport.setVisibility(View.GONE);
            binding.cardStudentResult.setVisibility(View.GONE);
            binding.cardCertImportDefault.setVisibility(View.GONE);
            binding.cardCertProcessing.setVisibility(View.GONE);
            binding.cardCertResult.setVisibility(View.GONE);
            binding.cardCertInvalidFormat.setVisibility(View.GONE);
            binding.cardCertTooLarge.setVisibility(View.GONE);
            binding.cardCertMissingStudent.setVisibility(View.GONE);
            binding.cardCertServerError.setVisibility(View.GONE);
            binding.cardCertExport.setVisibility(View.GONE);
            binding.toggleDataType.setEnabled(false);
            binding.toggleOperationMode.setEnabled(false);
        }
    }

    private void loadExistingStudents() {
        if (studentListenerHandle != null) {
            studentListenerHandle.remove();
        }
        studentListenerHandle = studentRepository.listenStudents(new StudentRepository.StudentListCallback() {
            @Override
            public void onSuccess(List<Student> students) {
                cachedStudents.clear();
                knownStudentIds.clear();
                if (students != null) {
                    cachedStudents.addAll(students);
                    for (Student s : students) {
                        if (s.getStudentId() != null) {
                            knownStudentIds.add(s.getStudentId().trim().toUpperCase(Locale.US));
                        }
                    }
                }
                if (binding != null) {
                    binding.chipStudentExportSpec.setText(getString(R.string.io_export_spec_format, cachedStudents.size()));
                    binding.chipCertExportSpec.setText(getString(
                            R.string.export_certificate_spec_format, getCachedCertificateCount()));
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Silently retain cached state
            }
        });
    }

    private void setupToggles() {
        binding.toggleDataType.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                updateViewVisibility();
            }
        });

        binding.toggleOperationMode.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                updateViewVisibility();
            }
        });
    }

    private void updateViewVisibility() {
        AppUser user = AuthRepositoryProvider.getRepository().getCurrentUser();
        if (user != null && user.isEmployee()) return;

        boolean isStudent = binding.toggleDataType.getCheckedButtonId() == R.id.btnTypeStudent;
        boolean isImport = binding.toggleOperationMode.getCheckedButtonId() == R.id.btnModeImport;

        if (isStudent) {
            binding.cardStudentImport.setVisibility(isImport ? View.VISIBLE : View.GONE);
            binding.cardStudentExport.setVisibility(!isImport ? View.VISIBLE : View.GONE);
            binding.cardStudentResult.setVisibility(View.GONE);

            // Hide cert cards
            hideCertCards();
        } else {
            binding.cardStudentImport.setVisibility(View.GONE);
            binding.cardStudentExport.setVisibility(View.GONE);
            binding.cardStudentResult.setVisibility(View.GONE);

            if (isImport) {
                binding.cardCertExport.setVisibility(View.GONE);
                if (binding.cardCertProcessing.getVisibility() != View.VISIBLE &&
                    binding.cardCertResult.getVisibility() != View.VISIBLE &&
                    binding.cardCertInvalidFormat.getVisibility() != View.VISIBLE &&
                    binding.cardCertTooLarge.getVisibility() != View.VISIBLE &&
                    binding.cardCertMissingStudent.getVisibility() != View.VISIBLE &&
                    binding.cardCertServerError.getVisibility() != View.VISIBLE) {
                    binding.cardCertImportDefault.setVisibility(View.VISIBLE);
                }
            } else {
                hideCertCards();
                binding.cardCertExport.setVisibility(View.VISIBLE);
            }
        }
    }

    private void hideCertCards() {
        binding.cardCertImportDefault.setVisibility(View.GONE);
        binding.cardCertProcessing.setVisibility(View.GONE);
        binding.cardCertResult.setVisibility(View.GONE);
        binding.cardCertInvalidFormat.setVisibility(View.GONE);
        binding.cardCertTooLarge.setVisibility(View.GONE);
        binding.cardCertMissingStudent.setVisibility(View.GONE);
        binding.cardCertServerError.setVisibility(View.GONE);
        binding.cardCertExport.setVisibility(View.GONE);
    }

    private void setupPickers() {
        studentCsvPicker = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                importStudentCsvFromUri(uri);
            }
        });

        certCsvPicker = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                handleCertFilePicked(uri);
            }
        });

        studentExportDocLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument("text/csv"), uri -> {
            if (uri != null) {
                writeCsvToUri(uri, StudentCsvHelper.exportToCsv(cachedStudents), "sinh viên");
            }
        });

        certExportDocLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument("text/csv"), uri -> {
            if (uri != null) {
                exportCertificatesToUri(uri);
            }
        });
    }

    private void setupStudentActions() {
        binding.btnChooseStudentCsv.setOnClickListener(v -> studentCsvPicker.launch("*/*"));

        binding.btnDownloadStudentTemplate.setOnClickListener(v -> {
            shareText("Mã SV,Họ và tên,Ngày sinh,Lớp,Khoa\n20000001,Nguyễn Văn An,15/05/2002,20050101,Công nghệ thông tin\n20000002,Trần Thị Bình,22/11/2002,20050102,Kỹ thuật phần mềm");
            Toast.makeText(requireContext(), "Mẫu CSV sinh viên đã sẵn sàng", Toast.LENGTH_SHORT).show();
        });

        binding.btnExportStudentCsv.setOnClickListener(v -> {
            String defaultFileName = "students_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".csv";
            try {
                studentExportDocLauncher.launch(defaultFileName);
            } catch (Exception e) {
                // Fallback to share intent if CreateDocument not supported
                String csvData = StudentCsvHelper.exportToCsv(cachedStudents);
                shareText(csvData);
                Toast.makeText(requireContext(), getString(R.string.csv_exported_toast, cachedStudents.size()), Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnShareStudentErrorReport.setOnClickListener(v -> {
            shareText(binding.tvStudentResultSummary.getText().toString() + "\n" + binding.tvStudentResultDetail.getText().toString());
        });
    }

    private void setupCertActions() {
        binding.btnChooseOtherCertFile.setOnClickListener(v -> certCsvPicker.launch("*/*"));
        binding.btnCertChooseOtherInvalidFormat.setOnClickListener(v -> certCsvPicker.launch("*/*"));
        binding.btnCertChooseOtherTooLarge.setOnClickListener(v -> certCsvPicker.launch("*/*"));
        binding.btnCertChooseOtherMissingStudent.setOnClickListener(v -> certCsvPicker.launch("*/*"));
        binding.btnCertChooseOtherAfterResult.setOnClickListener(v -> {
            hideCertCards();
            binding.cardCertImportDefault.setVisibility(View.VISIBLE);
        });

        binding.btnStartCertImport.setOnClickListener(v -> startCertificateImport());
        binding.btnCertRetryServerError.setOnClickListener(v -> startCertificateImport());

        binding.btnShareCertReport.setOnClickListener(v -> {
            String report = binding.chipCertTotal.getText() + " · " +
                    binding.chipCertSuccess.getText() + " · " +
                    binding.chipCertFailed.getText() + "\n\n" +
                    binding.tvCertErrorDetails.getText();
            shareText(report);
        });

        binding.btnExportCertCsv.setOnClickListener(v -> {
            String defaultFileName = "certificates_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".csv";
            try {
                certExportDocLauncher.launch(defaultFileName);
            } catch (Exception e) {
                exportAllCertificates();
            }
        });
    }

    private int getCachedCertificateCount() {
        int count = 0;
        for (Student student : cachedStudents) {
            if (student != null) {
                count += Math.max(0, student.getCertificateCount());
            }
        }
        return count;
    }

    private void handleCertFilePicked(@NonNull Uri uri) {
        selectedCertFileUri = uri;
        String fileName = getFileName(uri);
        selectedCertFileName = fileName;

        // Check file extension: .xlsx rejected
        if (fileName.toLowerCase(Locale.US).endsWith(".xlsx")) {
            hideCertCards();
            binding.cardCertInvalidFormat.setVisibility(View.VISIBLE);
            return;
        }

        // Check size
        long fileSize = getFileSize(uri);
        selectedCertFileSize = fileSize;
        if (fileSize > 5 * 1024 * 1024) { // > 5 MB
            hideCertCards();
            double mb = fileSize / (1024.0 * 1024.0);
            binding.tvCertTooLargeDesc.setText(getString(R.string.import_too_large_desc_format, String.format(Locale.US, "%.1f MB", mb)));
            binding.cardCertTooLarge.setVisibility(View.VISIBLE);
            return;
        }

        // Ready state
        hideCertCards();
        binding.tvCertFileName.setText(selectedCertFileName);
        double mb = fileSize > 0 ? (fileSize / (1024.0 * 1024.0)) : 0.1;
        binding.tvCertFileSize.setText(String.format(Locale.US, "%.1f MB · CSV UTF-8", mb));
        binding.cardCertImportDefault.setVisibility(View.VISIBLE);
    }

    private String getFileName(Uri uri) {
        if (uri == null) return "file.csv";
        Cursor cursor = null;
        try {
            cursor = requireContext().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    String name = cursor.getString(nameIndex);
                    if (name != null) return name;
                }
            }
        } catch (Exception ignored) {
        } finally {
            if (cursor != null) cursor.close();
        }
        String lastSegment = uri.getLastPathSegment();
        return lastSegment != null ? lastSegment : "file.csv";
    }

    private long getFileSize(Uri uri) {
        if (uri == null) return 0;
        Cursor cursor = null;
        try {
            cursor = requireContext().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (sizeIndex != -1) {
                    long size = cursor.getLong(sizeIndex);
                    if (size > 0) return size;
                }
            }
        } catch (Exception ignored) {
        } finally {
            if (cursor != null) cursor.close();
        }

        // Accurate fallback: stream byte counting
        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri)) {
            if (inputStream != null) {
                byte[] buf = new byte[8192];
                long total = 0;
                int r;
                while ((r = inputStream.read(buf)) != -1) {
                    total += r;
                    if (total > 6 * 1024 * 1024) return total;
                }
                return total;
            }
        } catch (Exception ignored) {
        }
        return 0;
    }

    private void startCertificateImport() {
        if (selectedCertFileUri == null) {
            Toast.makeText(requireContext(), "Vui lòng chọn tệp CSV trước khi nhập.", Toast.LENGTH_SHORT).show();
            return;
        }

        hideCertCards();
        binding.cardCertProcessing.setVisibility(View.VISIBLE);
        binding.progressCertImport.setProgress(5);
        binding.tvCertProgressText.setText(getString(R.string.import_progress_format, 0, 10));

        ioExecutor.execute(this::parseAndImportCertificates);
    }

    private void parseAndImportCertificates() {
        List<List<String>> rawRows;
        try (InputStream is = requireContext().getContentResolver().openInputStream(selectedCertFileUri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            rawRows = StudentCsvHelper.parseCsvRows(reader);
        } catch (Exception e) {
            mainHandler.post(() -> {
                if (!isAdded() || binding == null) return;
                hideCertCards();
                binding.cardCertServerError.setVisibility(View.VISIBLE);
            });
            return;
        }

        if (rawRows == null || rawRows.isEmpty()) {
            mainHandler.post(() -> {
                if (!isAdded() || binding == null) return;
                hideCertCards();
                Toast.makeText(requireContext(), "Tệp rỗng hoặc không có dữ liệu chứng chỉ.", Toast.LENGTH_SHORT).show();
                binding.cardCertImportDefault.setVisibility(View.VISIBLE);
            });
            return;
        }

        // Filter header row if present
        List<List<String>> dataRows = new ArrayList<>();
        boolean isFirst = true;
        for (List<String> row : rawRows) {
            if (row.isEmpty()) continue;
            if (isFirst) {
                isFirst = false;
                String first = row.get(0);
                if (first != null && (first.contains("Mã SV") || first.contains("StudentId") || first.contains("MSSV"))) {
                    continue;
                }
            }
            dataRows.add(row);
        }

        if (dataRows.isEmpty()) {
            mainHandler.post(() -> {
                if (!isAdded() || binding == null) return;
                hideCertCards();
                Toast.makeText(requireContext(), "Tệp chỉ có tiêu đề, không có dòng dữ liệu.", Toast.LENGTH_SHORT).show();
                binding.cardCertImportDefault.setVisibility(View.VISIBLE);
            });
            return;
        }

        int total = dataRows.size();
        final AtomicInteger success = new AtomicInteger(0);
        final AtomicInteger failed = new AtomicInteger(0);
        final AtomicInteger pendingWrites = new AtomicInteger(0);
        final StringBuilder errorLog = new StringBuilder();

        SimpleDateFormat stdSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date now = new Date();

        int lineNum = 2;
        List<Runnable> pendingOperations = new ArrayList<>();

        for (List<String> row : dataRows) {
            if (row.size() < 4) {
                failed.incrementAndGet();
                synchronized (errorLog) {
                    errorLog.append("• Dòng ").append(lineNum).append(" · Không đủ 4 cột thông tin chứng chỉ\n");
                }
                lineNum++;
                continue;
            }

            String mssv = row.get(0).trim().toUpperCase(Locale.US);
            String certName = row.get(1).trim();
            String rawDate = row.get(2).trim();
            String issuer = row.get(3).trim();

            if (mssv.isEmpty() || certName.isEmpty() || issuer.isEmpty()) {
                failed.incrementAndGet();
                synchronized (errorLog) {
                    errorLog.append("• Dòng ").append(lineNum).append(" · Thông tin chứng chỉ bị trống trường bắt buộc\n");
                }
                lineNum++;
                continue;
            }

            // Validate MSSV exists in system
            if (!knownStudentIds.isEmpty() && !knownStudentIds.contains(mssv)) {
                failed.incrementAndGet();
                synchronized (errorLog) {
                    errorLog.append("• Dòng ").append(lineNum).append(" · MSSV ").append(mssv).append(" không có trong hệ thống\n");
                }
                lineNum++;
                continue;
            }

            // Normalize and validate date
            String stdDate = StudentCsvHelper.normalizeDate(rawDate);
            try {
                Date d = stdSdf.parse(stdDate);
                if (d != null && d.after(now)) {
                    failed.incrementAndGet();
                    synchronized (errorLog) {
                        errorLog.append("• Dòng ").append(lineNum).append(" · Ngày cấp không được ở tương lai: ").append(stdDate).append("\n");
                    }
                    lineNum++;
                    continue;
                }
            } catch (Exception e) {
                failed.incrementAndGet();
                synchronized (errorLog) {
                    errorLog.append("• Dòng ").append(lineNum).append(" · Định dạng ngày không hợp lệ: ").append(rawDate).append("\n");
                }
                lineNum++;
                continue;
            }

            final int currentLine = lineNum;
            pendingWrites.incrementAndGet();
            pendingOperations.add(() -> {
                Certificate cert = new Certificate(null, certName, stdDate, issuer);
                certificateRepository.addCertificate(mssv, cert, new CertificateRepository.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        success.incrementAndGet();
                        checkImportCompletion(total, success.get(), failed.get(), pendingWrites.decrementAndGet(), errorLog);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        failed.incrementAndGet();
                        synchronized (errorLog) {
                            errorLog.append("• Dòng ").append(currentLine).append(" · ").append(errorMessage).append("\n");
                        }
                        checkImportCompletion(total, success.get(), failed.get(), pendingWrites.decrementAndGet(), errorLog);
                    }
                });
            });

            lineNum++;
        }

        if (pendingOperations.isEmpty()) {
            renderImportResult(total, success.get(), failed.get(), errorLog);
            return;
        }

        // Execute all valid certificate operations
        mainHandler.post(() -> {
            for (Runnable op : pendingOperations) {
                op.run();
            }
        });
    }

    private void checkImportCompletion(int total, int success, int failed, int remainingWrites, StringBuilder errorLog) {
        if (remainingWrites <= 0) {
            mainHandler.post(() -> renderImportResult(total, success, failed, errorLog));
        } else {
            mainHandler.post(() -> {
                if (!isAdded() || binding == null) return;
                int finished = success + failed;
                int progress = total > 0 ? (int) ((finished / (float) total) * 100) : 100;
                binding.progressCertImport.setProgress(progress);
                binding.tvCertProgressText.setText(getString(R.string.import_progress_format, finished, total));
            });
        }
    }

    private void renderImportResult(int total, int success, int failed, StringBuilder errorLog) {
        if (!isAdded() || binding == null) return;
        hideCertCards();
        binding.chipCertTotal.setText(getString(R.string.import_total) + ": " + total);
        binding.chipCertSuccess.setText(getString(R.string.import_success) + ": " + success);
        binding.chipCertFailed.setText(getString(R.string.import_failed) + ": " + failed);

        String errors = errorLog.toString().trim();
        if (errors.length() > 0) {
            binding.tvCertErrorDetails.setText(errors);
            binding.tvCertErrorDetails.setVisibility(View.VISIBLE);
        } else {
            binding.tvCertErrorDetails.setVisibility(View.GONE);
        }
        binding.cardCertResult.setVisibility(View.VISIBLE);
    }

    private void exportAllCertificates() {
        loadCertificateCsv(new CertificateCsvCallback() {
            @Override
            public void onSuccess(String csv) {
                if (!isAdded()) return;
                shareText(csv);
                Toast.makeText(requireContext(), "Đã chuẩn bị CSV chứng chỉ.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message) {
                showCertificateExportError(message);
            }
        });
    }

    private void exportCertificatesToUri(Uri uri) {
        loadCertificateCsv(new CertificateCsvCallback() {
            @Override
            public void onSuccess(String csv) {
                if (isAdded()) {
                    writeCsvToUri(uri, csv, "chứng chỉ");
                }
            }

            @Override
            public void onError(String message) {
                showCertificateExportError(message);
            }
        });
    }

    private interface CertificateCsvCallback {
        void onSuccess(String csv);
        void onError(String message);
    }

    private void loadCertificateCsv(CertificateCsvCallback callback) {
        List<Student> students = new ArrayList<>(cachedStudents);
        if (students.isEmpty()) {
            callback.onSuccess(StudentCsvHelper.exportCertificatesToCsv(
                    students, Collections.emptyMap()));
            return;
        }

        Map<String, List<Certificate>> certificatesByStudent = new ConcurrentHashMap<>();
        AtomicInteger remaining = new AtomicInteger(students.size());
        AtomicBoolean completed = new AtomicBoolean(false);

        for (Student student : students) {
            if (student == null || student.getStudentId() == null
                    || student.getStudentId().trim().isEmpty()) {
                if (completed.compareAndSet(false, true)) {
                    callback.onError("Không thể xuất vì có sinh viên thiếu MSSV.");
                }
                return;
            }

            String studentId = student.getStudentId().trim();
            certificateRepository.getCertificates(studentId,
                    new CertificateRepository.CertificateListCallback() {
                        @Override
                        public void onSuccess(List<Certificate> certificates) {
                            if (completed.get()) return;
                            certificatesByStudent.put(studentId,
                                    certificates == null ? new ArrayList<>() : new ArrayList<>(certificates));
                            if (remaining.decrementAndGet() == 0
                                    && completed.compareAndSet(false, true)) {
                                String csv = StudentCsvHelper.exportCertificatesToCsv(
                                        students, certificatesByStudent);
                                callback.onSuccess(csv);
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            if (completed.compareAndSet(false, true)) {
                                callback.onError("Không thể tải chứng chỉ của MSSV "
                                        + studentId + ": " + errorMessage);
                            }
                        }
                    });
        }
    }

    private void showCertificateExportError(String message) {
        if (isAdded()) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    private void writeCsvToUri(Uri uri, String content, String label) {
        if (!isAdded()) return;
        android.content.ContentResolver contentResolver = requireContext().getContentResolver();
        ioExecutor.execute(() -> {
            try (OutputStream os = contentResolver.openOutputStream(uri)) {
                if (os != null) {
                    os.write(content.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                    mainHandler.post(() -> {
                        if (getContext() != null) {
                            Toast.makeText(requireContext(), "Đã xuất tệp CSV " + label + " thành công.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (getContext() != null) {
                        Toast.makeText(requireContext(), "Lỗi khi lưu tệp: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void importStudentCsvFromUri(Uri uri) {
        ioExecutor.execute(() -> {
            List<Student> imported;
            try (InputStream is = requireContext().getContentResolver().openInputStream(uri);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                imported = StudentCsvHelper.importFromCsv(sb.toString());
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (getContext() != null) {
                        Toast.makeText(requireContext(), "Lỗi đọc tệp CSV: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }

            if (imported.isEmpty()) {
                mainHandler.post(() -> {
                    if (getContext() != null) {
                        Toast.makeText(requireContext(), "Tệp không chứa bản ghi sinh viên hợp lệ.", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }

            final int total = imported.size();
            final AtomicInteger success = new AtomicInteger(0);
            final AtomicInteger failed = new AtomicInteger(0);
            final AtomicInteger remaining = new AtomicInteger(total);

            for (Student s : imported) {
                studentRepository.addStudent(s, new StudentRepository.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        success.incrementAndGet();
                        checkStudentImportCompletion(total, success.get(), failed.get(), remaining.decrementAndGet());
                    }

                    @Override
                    public void onError(String errorMessage) {
                        failed.incrementAndGet();
                        checkStudentImportCompletion(total, success.get(), failed.get(), remaining.decrementAndGet());
                    }
                });
            }
        });
    }

    private void checkStudentImportCompletion(int total, int success, int failed, int remaining) {
        if (remaining <= 0) {
            mainHandler.post(() -> {
                if (!isAdded() || binding == null) return;
                binding.cardStudentResult.setVisibility(View.VISIBLE);
                binding.tvStudentResultSummary.setText(getString(R.string.import_summary_format));
                binding.tvStudentResultDetail.setText(
                        "Tổng cộng: " + total + " · Thành công: " + success + " · Thất bại: " + failed);
                Toast.makeText(requireContext(), "Đã nhập " + success + "/" + total + " sinh viên vào Firestore.", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void shareText(String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, getString(R.string.title_import_export));
        startActivity(shareIntent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (studentListenerHandle != null) {
            studentListenerHandle.remove();
            studentListenerHandle = null;
        }
        binding = null;
    }
}
