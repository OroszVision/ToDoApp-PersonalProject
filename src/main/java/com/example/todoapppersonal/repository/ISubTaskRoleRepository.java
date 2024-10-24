package com.example.todoapppersonal.repository;

import com.example.todoapppersonal.model.AppUser;
import com.example.todoapppersonal.model.SubTaskRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ISubTaskRoleRepository extends JpaRepository<SubTaskRole, Long> {

    Optional<SubTaskRole> findByUserId(Long userId);  // Nalezení SubTaskRole podle userId
}
