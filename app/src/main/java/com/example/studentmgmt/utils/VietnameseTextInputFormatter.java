package com.example.studentmgmt.utils;

/** Formats committed text without modifying an EditText while an IME is composing input. */
public final class VietnameseTextInputFormatter {

    private VietnameseTextInputFormatter() { }

    public static String capitalizeWords(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        boolean atWordStart = true;
        StringBuilder formatted = new StringBuilder(value.length());
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            if (atWordStart && Character.isLetter(character)) {
                character = Character.toUpperCase(character);
                atWordStart = false;
            } else if (Character.isWhitespace(character)) {
                atWordStart = true;
            } else {
                atWordStart = false;
            }
            formatted.append(character);
        }
        return formatted.toString();
    }
}
