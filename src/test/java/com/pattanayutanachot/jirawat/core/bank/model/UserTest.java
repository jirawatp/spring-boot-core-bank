package com.pattanayutanachot.jirawat.core.bank.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserDetailsImplementation() {
        Role customerRole = new Role(1L, RoleType.CUSTOMER);
        Role tellerRole = new Role(2L, RoleType.TELLER);

        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("securepassword")
                .citizenId("1234567890123")
                .thaiName("ทดสอบ")
                .englishName("Test User")
                .pin("123456")
                .roles(Set.of(customerRole, tellerRole))
                .build();

        assertEquals("test@example.com", user.getUsername());
        assertEquals("securepassword", user.getPassword());
        assertEquals("1234567890123", user.getCitizenId());
        assertEquals("ทดสอบ", user.getThaiName());
        assertEquals("Test User", user.getEnglishName());
        assertEquals("123456", user.getPin());
        assertNotNull(user.getRoles());
        assertEquals(2, user.getRoles().size());

        // Check granted authorities
        Set<String> expectedAuthorities = Set.of("ROLE_CUSTOMER", "ROLE_TELLER");
        Set<String> actualAuthorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(java.util.stream.Collectors.toSet());

        assertEquals(expectedAuthorities, actualAuthorities);
    }

    @Test
    void testAccountStatus() {
        User user = User.builder().build();

        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }
}