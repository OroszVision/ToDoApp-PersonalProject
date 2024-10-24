package com.example.todoapppersonal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MainTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private boolean completed;

    @OneToMany(mappedBy = "mainTask", cascade = CascadeType.ALL)  // Zpětný vztah na SubTask
    private List<SubTask> subTasks = new ArrayList<>();  // Všechny vedlejší úkoly k tomuto hlavnímu úkolu

    @OneToMany(mappedBy = "mainTask")  // Zpětný vztah na TaskRole
    private List<TaskRole> taskRoles;  // Všechny role přidělené k tomuto úkolu (majitelé, kolaboranti)

}
