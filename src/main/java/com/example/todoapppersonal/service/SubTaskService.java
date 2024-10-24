package com.example.todoapppersonal.service;

import com.example.todoapppersonal.dto.CreateSubTaskDto;
import com.example.todoapppersonal.dto.SubTaskResponseDto;
import com.example.todoapppersonal.dto.UpdateSubTaskDto;
import com.example.todoapppersonal.model.*;
import com.example.todoapppersonal.repository.IMainTaskRepository;
import com.example.todoapppersonal.repository.ISubTaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubTaskService {
    private final ISubTaskRepository subTaskRepository;
    private final IMainTaskRepository mainTaskRepository;
    private final SubTaskRoleService subTaskRoleService;

    public SubTaskResponseDto createSubTask(Long mainTaskId, CreateSubTaskDto subTaskDto, AppUser currentAppuser) {
        // Vytvoření nového vedlejšího úkolu
        SubTask subTask = new SubTask();
        subTask.setTitle(subTaskDto.getTitle());
        subTask.setDescription(subTaskDto.getDescription());

        // Přiřazení hlavního úkolu k vedlejšímu úkolu (pokud je to potřeba)
        MainTask mainTask = mainTaskRepository.findById(mainTaskId)
                .orElseThrow(() -> new ResourceAccessException("MainTask not found"));
        subTask.setMainTask(mainTask);

        // Uložení vedlejšího úkolu do databáze
        SubTask savedSubTask = subTaskRepository.save(subTask);

        subTaskRoleService.createSubTaskRole(currentAppuser, savedSubTask);

        // Vytvoření a vrácení odpovědi (SubTaskResponseDto)
        return new SubTaskResponseDto(
                savedSubTask.getId(),
                savedSubTask.getTitle(),
                savedSubTask.getDescription(),
                savedSubTask.isCompleted(),
                savedSubTask.getMainTask().getId()
        );
    }

    public SubTaskResponseDto updateSubTask(AppUser currentUser, Long id, UpdateSubTaskDto subTaskDto) throws AccessDeniedException {
        try {
            // Nejdříve zkontrolujte, zda existuje SubTask
            Optional<SubTask> existingSubTask = subTaskRepository.findById(id);

            if (existingSubTask.isPresent()) {
                // Zkontrolujte roli uživatele (OWNER)
                SubTaskRole subTaskRole = subTaskRoleService.findByUserId(currentUser.getId());
                if (subTaskRole.getRole() == Role.OWNER) {
                    SubTask subTaskToUpdate = existingSubTask.get();  // Získání existujícího SubTask

                    // Aktualizace existujícího SubTask
                    subTaskToUpdate.setTitle(subTaskDto.getTitle());  // Správný název
                    subTaskToUpdate.setDescription(subTaskDto.getDescription());
                    subTaskToUpdate.setCompleted(subTaskDto.isCompleted());

                    // Uložení aktualizovaného SubTask
                    SubTask savedSubTask = subTaskRepository.save(subTaskToUpdate);

                    // Vytvoření a vrácení odpovědi (SubTaskResponseDto)
                    return new SubTaskResponseDto(
                            savedSubTask.getId(),
                            savedSubTask.getTitle(),
                            savedSubTask.getDescription(),
                            savedSubTask.isCompleted(),
                            savedSubTask.getMainTask().getId()
                    );
                } else {
                    throw new AccessDeniedException("You don't have permission to update this SubTask.");
                }
            } else {
                throw new IllegalArgumentException("SubTask with the given ID does not exist.");
            }
        } catch (EntityNotFoundException ex) {
            throw new EntityNotFoundException("SubTask has not been found: " + ex.getMessage());
        } catch (AccessDeniedException ex) {
            throw ex;  // Přímo vyhoďte AccessDeniedException, není nutné ho přetypovat
        }
    }


    public void deleteSubTask(AppUser currentUser, Long subTaskId) throws AccessDeniedException {
        // Zkontrolujeme, jestli uživatel má oprávnění (ROLE.OWNER) před smazáním úkolu
        SubTaskRole subTaskRole = subTaskRoleService.findByUserId(currentUser.getId());

        if(subTaskRole.getRole() == Role.OWNER) {
            // Smažeme roli uživatele spojenou s podúkolem
            subTaskRoleService.deleteSubTaskRole(subTaskRole.getId());

            // Smažeme samotný podúkol podle ID
            subTaskRepository.deleteById(subTaskId);
        } else {
            // Pokud uživatel nemá oprávnění, vyhodíme výjimku
            throw new AccessDeniedException("You don't have permission to delete this sub-task");
        }
    }

}
