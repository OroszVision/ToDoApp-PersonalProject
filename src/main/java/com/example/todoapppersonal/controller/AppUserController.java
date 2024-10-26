package com.example.todoapppersonal.controller;

import com.example.todoapppersonal.model.AppUser;
import com.example.todoapppersonal.repository.IAppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/appusers")
public class AppUserController {

    private final IAppUserRepository appUserRepository;

    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@AuthenticationPrincipal AppUser currentUser,
                                         @RequestParam("query") String query) {
        // Kontrola autentizace: Zjistíme, zda je uživatel přihlášený
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated.");
        }

        try {
            // Vyhledávání uživatelů v databázi na základě dotazu
            List<AppUser> users = appUserRepository.findByUsernameContaining(query);

            // Pokud nenajdeme žádné uživatele odpovídající dotazu
            if (users.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No users found matching the query: " + query);
            }

            // Převod nalezených uživatelů na DTO
            List<AppUserDto> result = users.stream()
                    .map(user -> new AppUserDto(user.getUsername()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            // Zpracování nečekané chyby
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your request.");
        }
    }


}
