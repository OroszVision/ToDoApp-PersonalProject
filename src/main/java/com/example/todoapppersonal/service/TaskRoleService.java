package com.example.todoapppersonal.service;

import com.example.todoapppersonal.dto.CreateTaskRoleDto;
import com.example.todoapppersonal.model.MainTask;
import com.example.todoapppersonal.model.TaskRole;
import com.example.todoapppersonal.repository.ITaskRoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskRoleService {
    private final ITaskRoleRepository taskRoleRepository;

    public void createTaskRole(CreateTaskRoleDto taskRoleDto) {
        TaskRole newRole = new TaskRole();
        newRole.setUser(taskRoleDto.getUser());
        newRole.setMainTask(taskRoleDto.getMainTask());
        newRole.setRole(taskRoleDto.getRole());
        taskRoleRepository.save(newRole);  // Uložení nové role do databáze
    }

    public List<MainTask> findMainTasksByUserAndRole(Long userId) {
        return taskRoleRepository.findMainTasksByUserAndRole(userId);  // Voláme repository přes službu
    }

    public MainTask findMainTasksByUserAndTask(Long userId, Long taskId) {
        return taskRoleRepository.findMainTasksByUserAndTask(userId, taskId);


    }

    public void deleteTaskRole(Long userId, Long taskId) {
            try {
                // Najít role podle userId a taskId
                List<TaskRole> taskRoles = taskRoleRepository.findByUserIdAndMainTask_Id(userId, taskId);

                // Zkontrolovat, zda byly nějaké role nalezeny
                if (taskRoles.isEmpty()) {
                    throw new EntityNotFoundException("No roles found for userId: " + userId + " and taskId: " + taskId);
                }
                // Smazat všechny nalezené role
                taskRoleRepository.deleteAll(taskRoles);
            } catch (Exception e) {
                throw new RuntimeException("Failed to delete task roles: " + e.getMessage());
            }
    }
}
