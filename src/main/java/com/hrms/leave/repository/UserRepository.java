package com.hrms.leave.repository;

import com.hrms.leave.entity.Role;
import com.hrms.leave.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByManagerId(Long managerId);

    List<User> findByRole(Role role);

    boolean existsByEmail(String email);
}
