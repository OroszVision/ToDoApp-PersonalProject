package com.example.todoapppersonal.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateMainTaskDto {
    private String title;
    private String description;
    private LocalDate dueDate;
    private boolean completed;
}
