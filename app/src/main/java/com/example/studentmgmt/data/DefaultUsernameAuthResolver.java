package com.example.studentmgmt.data;

import com.example.studentmgmt.utils.ValidationUtils;

/**
 * Default implementation of UsernameAuthResolver.
 * Maps valid usernames to technical domain credentials and accepts email aliases.
 */
public class DefaultUsernameAuthResolver implements UsernameAuthResolver {

    public static final String DEFAULT_DOMAIN = "tdtu.edu.vn";
    private final String domain;

    public DefaultUsernameAuthResolver() {
        this(DEFAULT_DOMAIN);
    }

    public DefaultUsernameAuthResolver(String domain) {
        this.domain = (domain != null && !domain.trim().isEmpty()) ? domain.trim() : DEFAULT_DOMAIN;
    }

    @Override
    public String resolveAuthEmail(String identifier) {
        if (identifier == null) {
            return null;
        }
        String trimmed = identifier.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        if (trimmed.contains("@")) {
            return ValidationUtils.isValidEmail(trimmed)
                    ? trimmed.toLowerCase(java.util.Locale.ROOT)
                    : null;
        }

        if (ValidationUtils.isValidUsername(trimmed)) {
            // Valid username mapped to technical tenant email
            return trimmed.toLowerCase(java.util.Locale.ROOT) + "@" + domain;
        }

        return null;
    }
}
