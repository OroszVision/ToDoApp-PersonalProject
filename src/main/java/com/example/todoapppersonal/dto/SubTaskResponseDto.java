package com.example.todoapppersonal.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubTaskResponseDto {
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private Long mainTaskId;
}
