package com.pattanayutanachot.jirawat.core.bank.service;

import com.pattanayutanachot.jirawat.core.bank.dto.RegisterRequest;
import com.pattanayutanachot.jirawat.core.bank.model.Role;
import com.pattanayutanachot.jirawat.core.bank.model.RoleType;
import com.pattanayutanachot.jirawat.core.bank.model.User;
import com.pattanayutanachot.jirawat.core.bank.repository.RoleRepository;
import com.pattanayutanachot.jirawat.core.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new user with the default CUSTOMER role.
     *
     * @param request The registration request containing user details.
     * @return The newly created User object.
     */
    public User registerCustomer(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        Role customerRole = roleRepository.findByName(RoleType.CUSTOMER)
                .orElseThrow(() -> new IllegalStateException("CUSTOMER role not found"));

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(Collections.singleton(customerRole))
                .build();

        return userRepository.save(user);
    }

    /**
     * Registers a new TELLER user, only allowed if the requesting user is a SUPER_ADMIN.
     *
     * @param request The registration request containing user details.
     * @param adminUser The authenticated admin user.
     * @return The newly created User object.
     */
    public User registerTeller(RegisterRequest request, User adminUser) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        if (!isSuperAdmin(adminUser)) {
            throw new SecurityException("Only SUPER_ADMIN can register TELLER accounts.");
        }

        Role tellerRole = roleRepository.findByName(RoleType.TELLER)
                .orElseThrow(() -> new IllegalStateException("TELLER role not found"));

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(Collections.singleton(tellerRole))
                .build();

        return userRepository.save(user);
    }

    /**
     * Checks if a user has the SUPER_ADMIN role.
     *
     * @param user The user to check.
     * @return true if the user is a SUPER_ADMIN, false otherwise.
     */
    private boolean isSuperAdmin(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleType.SUPER_ADMIN);
    }

    /**
     * Retrieves a user by email.
     *
     * @param email The email of the user.
     * @return An Optional containing the user if found.
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}