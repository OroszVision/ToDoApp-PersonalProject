package com.example.todoapppersonal.dto;

import lombok.Data;

@Data
public class UpdateSubTaskDto {
    private String title;
    private String description;
    private boolean completed;
}
