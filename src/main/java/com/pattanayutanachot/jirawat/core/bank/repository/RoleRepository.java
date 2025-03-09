package com.pattanayutanachot.jirawat.core.bank.repository;

import com.pattanayutanachot.jirawat.core.bank.model.Role;
import com.pattanayutanachot.jirawat.core.bank.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
}