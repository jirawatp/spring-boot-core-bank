package com.pattanayutanachot.jirawat.core.bank.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void shouldCreateRoleWithDefaultConstructor() {
        Role role = new Role();

        assertNotNull(role);
        assertNull(role.getId());
        assertNull(role.getName());
    }

    @Test
    void shouldCreateRoleWithAllArgsConstructor() {
        Role role = new Role(1L, RoleType.CUSTOMER);

        assertNotNull(role);
        assertEquals(1L, role.getId());
        assertEquals(RoleType.CUSTOMER, role.getName());
    }

    @Test
    void shouldSetAndGetRoleProperties() {
        Role role = new Role();
        role.setId(2L);
        role.setName(RoleType.TELLER);

        assertEquals(2L, role.getId());
        assertEquals(RoleType.TELLER, role.getName());
    }
}