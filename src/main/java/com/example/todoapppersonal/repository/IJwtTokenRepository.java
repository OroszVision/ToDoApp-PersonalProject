package com.example.todoapppersonal.repository;

import com.example.todoapppersonal.model.AppUser;
import com.example.todoapppersonal.model.JwtToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IJwtTokenRepository extends JpaRepository<JwtToken,Long> {
    Optional<JwtToken> findByToken(String token);
    Optional<JwtToken> findByUser(AppUser user);
}
