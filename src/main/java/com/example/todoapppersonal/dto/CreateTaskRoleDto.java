package com.example.todoapppersonal.dto;

import com.example.todoapppersonal.model.AppUser;
import com.example.todoapppersonal.model.MainTask;
import com.example.todoapppersonal.model.Role;
import lombok.Data;

@Data
public class CreateTaskRoleDto {
    private AppUser user;  // Uživatel, který má roli
    private MainTask mainTask;  // Hlavní úkol, ke kterému se role vztahuje
    private Role role;  // Role uživatele (majitel, kolaborant)

}
