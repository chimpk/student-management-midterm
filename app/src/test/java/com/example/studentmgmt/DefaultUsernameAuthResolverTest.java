package com.example.studentmgmt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.example.studentmgmt.data.DefaultUsernameAuthResolver;

import org.junit.Before;
import org.junit.Test;

public class DefaultUsernameAuthResolverTest {

    private DefaultUsernameAuthResolver resolver;

    @Before
    public void setUp() {
        resolver = new DefaultUsernameAuthResolver("tdtu.edu.vn");
    }

    @Test
    public void validUsernames_areResolvedToDomainEmail() {
        assertEquals("admin@tdtu.edu.vn", resolver.resolveAuthEmail("admin"));
        assertEquals("manager@tdtu.edu.vn", resolver.resolveAuthEmail("manager"));
        assertEquals("employee@tdtu.edu.vn", resolver.resolveAuthEmail("employee"));
    }

    @Test
    public void identifiersWithAtSymbol_returnNull_failClosedUntilBackendProvisioned() {
        // Enforces Finding 3 & Review Round 3: All inputs containing '@' (aliases and tenant domain) must fail-closed
        assertNull(resolver.resolveAuthEmail("admin@tdtu.edu.vn"));
        assertNull(resolver.resolveAuthEmail("ADMIN@TDTU.EDU.VN"));
        assertNull(resolver.resolveAuthEmail("admin@university.edu.vn"));
        assertNull(resolver.resolveAuthEmail("manager@student.app"));
        assertNull(resolver.resolveAuthEmail("user123@gmail.com"));
    }

    @Test
    public void invalidIdentifiers_returnNull() {
        assertNull(resolver.resolveAuthEmail(null));
        assertNull(resolver.resolveAuthEmail(""));
        assertNull(resolver.resolveAuthEmail("   "));
        assertNull(resolver.resolveAuthEmail("ab")); // Too short for username
        assertNull(resolver.resolveAuthEmail("User With Spaces"));
    }
}
