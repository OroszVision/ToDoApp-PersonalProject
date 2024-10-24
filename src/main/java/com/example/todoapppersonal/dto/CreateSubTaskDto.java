package com.example.todoapppersonal.dto;

import lombok.Data;

@Data
public class CreateSubTaskDto {
    private String title;  // Název vedlejšího úkolu
    private String description;  // Popis vedlejšího úkolu
}

