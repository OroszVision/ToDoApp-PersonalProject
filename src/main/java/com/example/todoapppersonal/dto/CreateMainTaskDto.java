package com.example.todoapppersonal.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateMainTaskDto {
    private String title;
    private String description;
    private LocalDate dueDate;
}
