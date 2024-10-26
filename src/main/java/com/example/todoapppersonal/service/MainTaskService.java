package com.example.todoapppersonal.service;

import com.example.todoapppersonal.dto.*;
import com.example.todoapppersonal.model.*;
import com.example.todoapppersonal.repository.IMainTaskRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainTaskService {
    private final IMainTaskRepository mainTaskRepository;
    private final TaskRoleService taskRoleService;
    private final FriendshipService friendshipService;

    public MainTaskResponseDto createMainTask(CreateMainTaskDto taskDto, AppUser currentUser) {
        try {
            // Vytvoření nové instance MainTask
            MainTask mainTask = new MainTask();
            mainTask.setTitle(taskDto.getTitle());
            mainTask.setDescription(taskDto.getDescription());
            mainTask.setDueDate(taskDto.getDueDate());

            // Uložení nového úkolu do databáze
            MainTask savedTask = mainTaskRepository.save(mainTask);

            // Vytvoření role pro aktuálního uživatele (vlastník úkolu)
            CreateTaskRoleDto createTaskRoleDto = new CreateTaskRoleDto();
            createTaskRoleDto.setMainTask(savedTask);
            createTaskRoleDto.setUser(currentUser);
            createTaskRoleDto.setRole(Role.OWNER);

            // Uložení TaskRole přes service
            taskRoleService.createTaskRole(createTaskRoleDto);

            // Vytvoření response DTO
            MainTaskResponseDto responseDto = new MainTaskResponseDto();
            responseDto.setId(savedTask.getId());
            responseDto.setTitle(savedTask.getTitle());
            responseDto.setDescription(savedTask.getDescription());
            responseDto.setDueDate(savedTask.getDueDate());

            return responseDto;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create task", e);
        }
    }

    public List<MainTaskWithSubTasksDto> getMainTasksWithSubTasks(AppUser currentUser) {
        List<MainTask> mainTasks = taskRoleService.findMainTasksByUserAndRole(currentUser.getId());// Získání hlavních úkolů pro uživatele
        return mainTasks.stream()
                .map(this::mapToMainTaskWithSubTasksDto)  // Mapování na DTO
                .collect(Collectors.toList());
    }

    public List<MainTaskResponseDto> getUsersMainTasks(AppUser currentUser) {
        try {
            // Načtení všech hlavních úkolů, kde je uživatel vlastníkem nebo spolupracovníkem přes TaskRoleService
            List<MainTask> tasks = taskRoleService.findMainTasksByUserAndRole(currentUser.getId());

            // Převedení seznamu MainTask na MainTaskResponseDto
            return tasks.stream()
                    .map(task -> new MainTaskResponseDto(
                            task.getId(),
                            task.getTitle(),
                            task.getDescription(),
                            task.getDueDate()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user's main tasks", e);
        }
    }

    public MainTaskResponseDto getUsersMainTaskById(AppUser currentUser, Long id) {
    try{
        MainTask task = taskRoleService.findMainTasksByUserAndTask(currentUser.getId(), id);

        // Vytvoření response DTO
        MainTaskResponseDto responseDto = new MainTaskResponseDto();
        responseDto.setId(task.getId());
        responseDto.setTitle(task.getTitle());
        responseDto.setDescription(task.getDescription());
        responseDto.setDueDate(task.getDueDate());
        return responseDto;
    }catch (EntityNotFoundException ex){
        throw new EntityNotFoundException("Main Task has not been found" + ex.getMessage());
    }
    }

    public MainTaskResponseDto updateUsersMainTask(AppUser currentUser, Long id, UpdateMainTaskDto updateMainTaskDto) {
        try{
            MainTask task = taskRoleService.findMainTasksByUserAndTask(currentUser.getId(), id);

            task.setTitle(updateMainTaskDto.getTitle());
            task.setDescription(updateMainTaskDto.getDescription());
            task.setDueDate(updateMainTaskDto.getDueDate());
            task.setCompleted(updateMainTaskDto.isCompleted());

            MainTask updatedTask =mainTaskRepository.save(task);

            return new MainTaskResponseDto(
                    updatedTask.getId(),
                    updatedTask.getTitle(),
                    updatedTask.getDescription(),
                    updatedTask.getDueDate()
            );
        }catch (EntityNotFoundException ex){
            throw new EntityNotFoundException("Task not found for user: " + currentUser.getUsername() + ", taskId: " + id);
        }
    }

    @Transactional  // Atomické zpracování operace
    public void deleteUsersMainTask(AppUser currentUser, Long taskId) {
        try {
            // Ověření, že uživatel má přístup k úkolu
            MainTask task = taskRoleService.findMainTasksByUserAndTask(currentUser.getId(), taskId);

            // Smazání všech TaskRole vazeb k úkolu
            taskRoleService.deleteTaskRole(currentUser.getId(), taskId);

            // Smazání samotného úkolu
            mainTaskRepository.delete(task);
        } catch (EntityNotFoundException ex) {
            // Pokud úkol nebyl nalezen nebo uživatel nemá oprávnění
            throw new EntityNotFoundException("Task not found or access denied for user: " + currentUser.getUsername() + ", taskId: " + taskId);
        } catch (Exception ex) {
            // Obecné zachycení jakýchkoli jiných výjimek
            throw new RuntimeException("Failed to delete task: " + ex.getMessage());
        }
    }

    public void assignCollaborator(Long taskId, AppUser owner, AppUser collaborator) {
        // Ověření, že jsou přátelé
        if (!friendshipService.getFriends(owner).contains(collaborator)) {
            throw new RuntimeException("User is not a friend");
        }

        MainTask task = mainTaskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        CreateTaskRoleDto createTaskRoleDto = new CreateTaskRoleDto();
        createTaskRoleDto.setMainTask(task);
        createTaskRoleDto.setUser(collaborator);
        createTaskRoleDto.setRole(Role.COLLABORATOR);

        taskRoleService.createTaskRole(createTaskRoleDto);
    }

    private MainTaskWithSubTasksDto mapToMainTaskWithSubTasksDto(MainTask mainTask) {
        MainTaskWithSubTasksDto dto = new MainTaskWithSubTasksDto();
        dto.setId(mainTask.getId());
        dto.setTitle(mainTask.getTitle());
        dto.setDescription(mainTask.getDescription());
        dto.setCompleted(mainTask.isCompleted());
        dto.setDueDate(mainTask.getDueDate());
        dto.setSubTasks(mainTask.getSubTasks().stream()
                .map(subTask -> {
                    SubTaskResponseDto subTaskDto = new SubTaskResponseDto();
                    subTaskDto.setId(subTask.getId());
                    subTaskDto.setTitle(subTask.getTitle());
                    subTaskDto.setDescription(subTask.getDescription());
                    subTaskDto.setCompleted(subTask.isCompleted());
                    return subTaskDto;
                })
                .collect(Collectors.toList()));
        return dto;
    }
}
