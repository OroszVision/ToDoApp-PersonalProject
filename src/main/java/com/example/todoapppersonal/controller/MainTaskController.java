package com.example.todoapppersonal.controller;


import com.example.todoapppersonal.dto.CreateMainTaskDto;
import com.example.todoapppersonal.dto.MainTaskResponseDto;
import com.example.todoapppersonal.dto.MainTaskWithSubTasksDto;
import com.example.todoapppersonal.dto.UpdateMainTaskDto;
import com.example.todoapppersonal.model.AppUser;
import com.example.todoapppersonal.service.MainTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/maintasks")
public class MainTaskController {

    private final MainTaskService mainTaskService;

    @PostMapping
    public ResponseEntity<MainTaskResponseDto> create(@RequestBody CreateMainTaskDto taskDto, @AuthenticationPrincipal AppUser currentUser){
        try{
            MainTaskResponseDto savedTask =  mainTaskService.createMainTask(taskDto,currentUser);
            return ResponseEntity.status(HttpStatus.OK).body(savedTask);
        }catch (InvalidParameterException ex){
            throw new InvalidParameterException("Invalid Input Data:" + ex.getMessage());
        }
    }

    @GetMapping("/usersMainTasks")
    public ResponseEntity<List<MainTaskResponseDto>> getUsersMainTasks(@AuthenticationPrincipal AppUser currentUser) {
        List<MainTaskResponseDto> tasks = mainTaskService.getUsersMainTasks(currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MainTaskResponseDto> getUsersMainTaskById(@AuthenticationPrincipal AppUser currentUser,@PathVariable Long id){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(mainTaskService.getUsersMainTaskById(currentUser, id));
        }catch (RuntimeException ex){
            throw new RuntimeException("Something went wrong" + ex.getMessage());
        }
    }

    @GetMapping("maintask/subtasks")
    public ResponseEntity<List<MainTaskWithSubTasksDto>> getUsersMainTasksWithSubTasks(@AuthenticationPrincipal AppUser currentUser){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(mainTaskService.getMainTasksWithSubTasks(currentUser));
        }catch(RuntimeException ex){
            throw new RuntimeException("Something went wrong" + ex.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<MainTaskResponseDto> updateUsersMainTask(@AuthenticationPrincipal AppUser currentUser, @PathVariable Long id, UpdateMainTaskDto updateMainTaskDto){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(mainTaskService.updateUsersMainTask(currentUser,id, updateMainTaskDto));
        }catch(InvalidParameterException ex){
            throw new InvalidParameterException("You dont have persmissions to delete this task" + ex.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteUsersMainTask(@AuthenticationPrincipal AppUser currentUser, @PathVariable Long id){
        try{
            mainTaskService.deleteUsersMainTask(currentUser,id);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }catch (InvalidParameterException ex){
            throw new InvalidParameterException("Main task was not found" + ex.getMessage());
        }
    }

}
