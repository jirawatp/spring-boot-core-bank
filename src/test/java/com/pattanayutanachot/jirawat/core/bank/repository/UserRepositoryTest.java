package com.pattanayutanachot.jirawat.core.bank.repository;

import com.pattanayutanachot.jirawat.core.bank.model.Role;
import com.pattanayutanachot.jirawat.core.bank.model.RoleType;
import com.pattanayutanachot.jirawat.core.bank.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testFindByEmail() {
        User user = userRepository.save(User.builder()
                .email("test@example.com")
                .password("securepassword")
                .citizenId("1234567890123")
                .roles(Set.of(roleRepository.save(new Role(null, RoleType.CUSTOMER))))
                .build());

        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    void testFindByCitizenId() {
        User user = userRepository.save(User.builder()
                .email("test2@example.com")
                .password("securepassword")
                .citizenId("9876543210987")
                .roles(Set.of(roleRepository.save(new Role(null, RoleType.CUSTOMER))))
                .build());

        Optional<User> foundUser = userRepository.findByCitizenId("9876543210987");

        assertTrue(foundUser.isPresent());
        assertEquals("9876543210987", foundUser.get().getCitizenId());
    }

    @Test
    void testExistsByEmail() {
        userRepository.save(User.builder()
                .email("existing@example.com")
                .password("securepassword")
                .citizenId("5678901234567")
                .roles(Set.of(roleRepository.save(new Role(null, RoleType.CUSTOMER))))
                .build());

        assertTrue(userRepository.existsByEmail("existing@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }
}