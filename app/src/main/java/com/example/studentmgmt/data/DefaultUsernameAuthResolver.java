package com.example.studentmgmt.data;

import com.example.studentmgmt.utils.ValidationUtils;

/**
 * Default implementation of UsernameAuthResolver.
 * Maps valid usernames to technical domain credentials.
 * Email aliases are isolated and safely rejected with TODO(auth-backend)
 * until a trusted resolver/directory backend is provisioned.
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
            // Strictly fail-closed for all inputs containing '@' until a trusted directory backend is provisioned
            return null;
        }

        if (ValidationUtils.isValidUsername(trimmed)) {
            // Valid username mapped to technical tenant email
            return trimmed.toLowerCase(java.util.Locale.ROOT) + "@" + domain;
        }

        return null;
    }
}
