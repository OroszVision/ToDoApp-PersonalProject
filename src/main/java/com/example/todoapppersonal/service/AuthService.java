package com.example.todoapppersonal.service;

import com.example.todoapppersonal.dto.AuthRequest;
import com.example.todoapppersonal.dto.AuthResponse;
import com.example.todoapppersonal.dto.RegisterRequest;
import com.example.todoapppersonal.model.AppUser;
import com.example.todoapppersonal.model.JwtToken;
import com.example.todoapppersonal.repository.IAppUserRepository;
import com.example.todoapppersonal.repository.IJwtTokenRepository;
import com.example.todoapppersonal.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final IAppUserRepository appUserRepository;
    private final IJwtTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        try {
            AppUser newUser = new AppUser();
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
            newUser.setEmail(request.getEmail());
            newUser.setUsername(request.getUsername());
            appUserRepository.save(newUser);
            return new AuthResponse("User registered successfully!", newUser.getUsername());
        } catch (InvalidParameterException ex) {
            throw new InvalidParameterException("Your Inputted data were invalid: " + ex.getMessage());
        }
    }

    public AuthResponse authenticate(AuthRequest request) {
        try {
            // Authenticate the user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Load the user
            UserDetails userDetails = loadUserByUsername(request.getUsername());
            AppUser appUser = (AppUser) userDetails;

            // Generate a new JWT token
            String token = jwtUtil.generateToken(userDetails.getUsername());

            // Check if the user already has a valid token
            var existingTokenOpt = tokenRepository.findByUser(appUser);
            if (existingTokenOpt.isPresent()) {
                JwtToken existingToken = existingTokenOpt.get();
                // If the existing token is not expired, mark it as expired
                if (!existingToken.isExpired()) {
                    existingToken.setExpired(true);
                    existingToken.setRevoked(true);
                    tokenRepository.save(existingToken); // Save changes to mark it as expired
                }
            }

            // Save the new token to the database
            JwtToken jwtToken = new JwtToken();
            jwtToken.setToken(token);
            jwtToken.setExpired(false);
            jwtToken.setRevoked(false);
            jwtToken.setUser(appUser);
            tokenRepository.save(jwtToken);

            return new AuthResponse("Authentication successful!", token);
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }


    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return; // If token is not present, do nothing
        }

        jwt = authHeader.substring(7); // Remove "Bearer " part

        System.out.println("JWT Token: " + jwt); // Log the token for debugging

        var storedToken = tokenRepository.findByToken(jwt).orElse(null);

        if (storedToken != null) {
            // Set the token as expired and revoked
            storedToken.setExpired(true);
            storedToken.setRevoked(true);

            // Save changes to the repository
            tokenRepository.save(storedToken);

            // Clear the security context
            SecurityContextHolder.clearContext();
            System.out.println("User logged out successfully.");
        } else {
            System.out.println("Token not found in the repository.");
        }
    }

    public UserDetails loadUserByUsername(String username) {
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
