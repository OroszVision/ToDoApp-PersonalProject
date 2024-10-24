package com.example.todoapppersonal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MainTaskResponseDto {
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
}
