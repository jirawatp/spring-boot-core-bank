package com.pattanayutanachot.jirawat.core.bank.repository;

import com.pattanayutanachot.jirawat.core.bank.model.Role;
import com.pattanayutanachot.jirawat.core.bank.model.RoleType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testFindByName() {
        Role role = roleRepository.save(new Role(null, RoleType.CUSTOMER));

        Optional<Role> foundRole = roleRepository.findByName(RoleType.CUSTOMER);
        assertTrue(foundRole.isPresent());
        assertEquals(RoleType.CUSTOMER, foundRole.get().getName());
    }

    @Test
    void testFindByName_NotFound() {
        Optional<Role> foundRole = roleRepository.findByName(RoleType.SUPER_ADMIN);
        assertFalse(foundRole.isPresent());
    }
}