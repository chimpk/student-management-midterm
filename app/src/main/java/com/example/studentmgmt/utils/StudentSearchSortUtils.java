package com.example.studentmgmt.utils;

import com.example.studentmgmt.model.Student;

import java.text.Collator;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/** Client-side Vietnamese-aware student filtering and sorting. */
public final class StudentSearchSortUtils {
    public enum SortOption {
        STUDENT_ID_ASC, NAME_ASC, NAME_DESC, DATE_OF_BIRTH_ASC, DATE_OF_BIRTH_DESC,
        CLASS_ASC, CLASS_DESC
    }

    private static final Pattern MARKS = Pattern.compile("\\p{M}+");
    private static final Pattern TOKENS = Pattern.compile("\\s+");
    private static final Collator VI = Collator.getInstance(new Locale("vi", "VN"));

    private StudentSearchSortUtils() {}

    public static String normalize(String value) {
        if (value == null) return "";
        String decomposed = Normalizer.normalize(value, Normalizer.Form.NFD);
        return MARKS.matcher(decomposed).replaceAll("").replace('đ', 'd').replace('Đ', 'D')
                .toLowerCase(Locale.ROOT).trim();
    }

    /** Each query word may match any of name, student ID, class, or faculty. */
    public static boolean matches(Student student, String query) {
        if (student == null) return false;
        String normalized = normalize(query);
        if (normalized.isEmpty()) return true;
        String[] fields = { normalize(student.getName()), normalize(student.getStudentId()),
                normalize(student.getClassName()), normalize(student.getFaculty()) };
        for (String token : TOKENS.split(normalized)) {
            boolean found = nameContainsWordPrefix(fields[0], token)
                    || fields[1].contains(token) || fields[2].contains(token)
                    || (token.length() >= 3 && fields[3].contains(token));
            if (!found) return false;
        }
        return true;
    }

    private static boolean nameContainsWordPrefix(String name, String queryToken) {
        for (String word : TOKENS.split(name)) {
            if (queryToken.length() <= 2 ? word.startsWith(queryToken) : word.contains(queryToken)) return true;
        }
        return false;
    }

    public static List<Student> filter(List<Student> students, String query) {
        List<Student> result = new ArrayList<>();
        if (students != null) {
            for (Student student : students) if (matches(student, query)) result.add(student);
        }
        return result;
    }

    public static List<Student> sortedCopy(List<Student> students, SortOption option) {
        List<Student> result = students == null ? new ArrayList<>() : new ArrayList<>(students);
        result.sort(comparator(option == null ? SortOption.STUDENT_ID_ASC : option));
        return result;
    }

    public static Comparator<Student> comparator(SortOption option) {
        Comparator<Student> byId = Comparator.comparing(s -> safe(s.getStudentId()));
        switch (option) {
            case NAME_ASC: return byName().thenComparing(byId);
            case NAME_DESC: return byName().reversed().thenComparing(byId);
            case DATE_OF_BIRTH_ASC: return Comparator.<Student, String>comparing(s -> safe(s.getDateOfBirth()))
                    .thenComparing(byId);
            case DATE_OF_BIRTH_DESC: return Comparator.<Student, String>comparing(s -> safe(s.getDateOfBirth()))
                    .reversed().thenComparing(byId);
            case CLASS_ASC: return Comparator.<Student, String>comparing(s -> safe(s.getClassName()))
                    .thenComparing(byId);
            case CLASS_DESC: return Comparator.<Student, String>comparing(s -> safe(s.getClassName()))
                    .reversed().thenComparing(byId);
            case STUDENT_ID_ASC:
            default: return byId;
        }
    }

    private static Comparator<Student> byName() {
        return (a, b) -> {
            int byGivenName = VI.compare(givenName(a.getName()), givenName(b.getName()));
            return byGivenName != 0 ? byGivenName : VI.compare(safe(a.getName()), safe(b.getName()));
        };
    }

    private static String givenName(String name) {
        String fullName = safe(name).trim();
        int lastSpace = fullName.lastIndexOf(' ');
        return lastSpace < 0 ? fullName : fullName.substring(lastSpace + 1);
    }

    private static String safe(String value) { return value == null ? "" : value; }
}
