package com.pattanayutanachot.jirawat.core.bank.security;

import com.pattanayutanachot.jirawat.core.bank.model.Role;
import com.pattanayutanachot.jirawat.core.bank.model.RoleType;
import com.pattanayutanachot.jirawat.core.bank.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {

    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .email("test@example.com")
                .password("securepassword")
                .roles(Set.of(new Role(1L, RoleType.CUSTOMER)))
                .build();
        customUserDetails = new CustomUserDetails(user);
    }

    @Test
    void testGetAuthorities() {
        var authorities = customUserDetails.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream().map(GrantedAuthority::getAuthority).anyMatch(role -> role.equals("ROLE_CUSTOMER")));
    }

    @Test
    void testGetPassword() {
        assertEquals("securepassword", customUserDetails.getPassword());
    }

    @Test
    void testGetUsername() {
        assertEquals("test@example.com", customUserDetails.getUsername());
    }

    @Test
    void testIsAccountNonExpired() {
        assertTrue(customUserDetails.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        assertTrue(customUserDetails.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        assertTrue(customUserDetails.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        assertTrue(customUserDetails.isEnabled());
    }
}