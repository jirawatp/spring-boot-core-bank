package com.pattanayutanachot.jirawat.core.bank.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTypeTest {

    @Test
    void shouldContainExpectedRoleTypes() {
        RoleType[] expectedRoles = {RoleType.CUSTOMER, RoleType.TELLER, RoleType.SUPER_ADMIN};

        assertArrayEquals(expectedRoles, RoleType.values());
    }

    @Test
    void shouldReturnCorrectRoleTypeByName() {
        assertEquals(RoleType.CUSTOMER, RoleType.valueOf("CUSTOMER"));
        assertEquals(RoleType.TELLER, RoleType.valueOf("TELLER"));
        assertEquals(RoleType.SUPER_ADMIN, RoleType.valueOf("SUPER_ADMIN"));
    }
}