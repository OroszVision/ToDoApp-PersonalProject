package com.example.todoapppersonal.controller;

import com.example.todoapppersonal.dto.CreateSubTaskDto;
import com.example.todoapppersonal.dto.SubTaskResponseDto;
import com.example.todoapppersonal.dto.UpdateSubTaskDto;
import com.example.todoapppersonal.model.AppUser;
import com.example.todoapppersonal.service.SubTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/subtasks")
public class SubTaskController {
    private final SubTaskService subTaskService;

    @PostMapping("/main-tasks/{mainTaskId}/sub-tasks")
    public ResponseEntity<SubTaskResponseDto> createSubTask(
            @AuthenticationPrincipal AppUser currentUser,
            @PathVariable Long mainTaskId,
            @RequestBody CreateSubTaskDto subTaskDto) {

        SubTaskResponseDto subtask = subTaskService.createSubTask(mainTaskId, subTaskDto, currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(subtask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubTaskResponseDto> updateSubTask(@AuthenticationPrincipal AppUser currentUser,
                                                            @PathVariable Long id,
                                                            @RequestBody UpdateSubTaskDto subTaskDto) {
        try {
            // Aktualizace úkolu pomocí služby
            SubTaskResponseDto updatedTask = subTaskService.updateSubTask(currentUser, id, subTaskDto);

            // Pokud je aktualizace úspěšná, vrátíme HTTP status 200 OK a aktualizovaný úkol
            return ResponseEntity.ok(updatedTask);

        } catch (IllegalArgumentException e) {
            // Pokud úkol s daným ID neexistuje, vrátíme HTTP status 404 Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        } catch (AccessDeniedException e) {
            // Pokud uživatel nemá oprávnění, vrátíme HTTP status 403 Forbidden
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);

        } catch (Exception e) {
            // Zachycení obecné chyby a vrácení statusu 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{subTaskId}")
    public ResponseEntity<Void> deleteSubTask(@AuthenticationPrincipal AppUser currentUser, @PathVariable Long subTaskId) throws IllegalAccessException, AccessDeniedException {
        subTaskService.deleteSubTask(currentUser, subTaskId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }


}
