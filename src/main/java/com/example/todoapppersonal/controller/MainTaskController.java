package com.example.todoapppersonal.controller;

import com.example.todoapppersonal.dto.*;
import com.example.todoapppersonal.model.AppUser;
import com.example.todoapppersonal.repository.IAppUserRepository;
import com.example.todoapppersonal.service.FriendshipService;
import com.example.todoapppersonal.service.MainTaskService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/maintasks")
public class MainTaskController {

    private final MainTaskService mainTaskService;
    private final IAppUserRepository appUserRepository;
    private final FriendshipService friendshipService;
    @PostMapping
    public ResponseEntity<MainTaskResponseDto> create(@Valid @RequestBody CreateMainTaskDto taskDto,
                                                      @AuthenticationPrincipal AppUser currentUser) {
        MainTaskResponseDto savedTask = mainTaskService.createMainTask(taskDto, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }

    @GetMapping("/usersMainTasks")
    public ResponseEntity<List<MainTaskResponseDto>> getUsersMainTasks(@AuthenticationPrincipal AppUser currentUser) {
        List<MainTaskResponseDto> tasks = mainTaskService.getUsersMainTasks(currentUser);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MainTaskResponseDto> getUsersMainTaskById(@AuthenticationPrincipal AppUser currentUser,
                                                                    @PathVariable Long id) {
        MainTaskResponseDto task = mainTaskService.getUsersMainTaskById(currentUser, id);
        return ResponseEntity.ok(task);
    }

    @GetMapping("maintask/subtasks")
    public ResponseEntity<List<MainTaskWithSubTasksDto>> getUsersMainTasksWithSubTasks(@AuthenticationPrincipal AppUser currentUser) {
        List<MainTaskWithSubTasksDto> tasksWithSubTasks = mainTaskService.getMainTasksWithSubTasks(currentUser);
        return ResponseEntity.ok(tasksWithSubTasks);
    }

    @PutMapping("{id}")
    public ResponseEntity<MainTaskResponseDto> updateUsersMainTask(@AuthenticationPrincipal AppUser currentUser,
                                                                   @PathVariable Long id,
                                                                   @Valid @RequestBody UpdateMainTaskDto updateMainTaskDto) {
        MainTaskResponseDto updatedTask = mainTaskService.updateUsersMainTask(currentUser, id, updateMainTaskDto);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteUsersMainTask(@AuthenticationPrincipal AppUser currentUser,
                                                    @PathVariable Long id) {
        mainTaskService.deleteUsersMainTask(currentUser, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint pro přidání spolupracovníka k úkolu.
     */
    @PostMapping("/{taskId}/add-collaborator/{collaboratorId}")
    public ResponseEntity<Void> addCollaborator(@PathVariable Long taskId,
                                                @PathVariable Long collaboratorId,
                                                @AuthenticationPrincipal AppUser owner) {
        // Najde uživatele podle jeho ID
        Optional<AppUser> collaborator = appUserRepository.findById(collaboratorId);

        if (!collaborator.isPresent()) {
            throw new EntityNotFoundException("Collaborator has not been found!");
        }

        // Ověření, zda jsou uživatelé přátelé
        if (!friendshipService.getFriends(owner).contains(collaborator.get())) {
            throw new RuntimeException("User is not a friend"); // Můžete vytvořit vlastní výjimku
        }

        // Přidá spolupracovníka k úkolu
        mainTaskService.assignCollaborator(taskId, owner, collaborator.get());
        return ResponseEntity.ok().build();
    }

}

