package com.example.todoapppersonal.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MainTaskWithSubTasksDto {
    private Long id;  // ID hlavního úkolu
    private String title;  // Název hlavního úkolu
    private String description;  // Popis hlavního úkolu
    private boolean completed;  // Stav dokončení
    private LocalDate dueDate;
    private List<SubTaskResponseDto> subTasks;  // Seznam vedlejších úkolů
}

