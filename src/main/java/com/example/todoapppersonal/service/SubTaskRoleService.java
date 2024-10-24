package com.example.todoapppersonal.service;

import com.example.todoapppersonal.model.AppUser;
import com.example.todoapppersonal.model.Role;
import com.example.todoapppersonal.model.SubTask;
import com.example.todoapppersonal.model.SubTaskRole;
import com.example.todoapppersonal.repository.ISubTaskRoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubTaskRoleService {
    private final ISubTaskRoleRepository subTaskRoleRepository;


    public void createSubTaskRole(AppUser currentAppuser, SubTask savedSubTask) {
        // Přiřazení role pro currentAppuser (např. jako OWNER) k novému vedlejšímu úkolu
        SubTaskRole subTaskRole = new SubTaskRole();
        subTaskRole.setUser(currentAppuser);
        subTaskRole.setSubTask(savedSubTask);
        subTaskRole.setRole(Role.OWNER); // Nebo jiná logika pro roli

        subTaskRoleRepository.save(subTaskRole);
    }

    public SubTaskRole findByUserId(Long userId) {
        try{
            // Nalezení role uživatele podle jeho ID
            return subTaskRoleRepository.findByUserId(userId)
                    .orElseThrow(() -> new EntityNotFoundException("SubTaskRole not found for user ID: " + userId));
        }catch(EntityNotFoundException ex){
            throw new EntityNotFoundException("User with connection to Subtask has not been found:" + ex.getMessage() );}
    }

    public void deleteSubTaskRole(Long id) {
        try{
            subTaskRoleRepository.deleteById(id);
        }catch(EntityNotFoundException ex){
            throw new EntityNotFoundException("SubTaskRole has not been found" + ex.getMessage() );
        }
    }
}
