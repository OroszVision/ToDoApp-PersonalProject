package com.example.todoapppersonal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SubTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "main_task_id", nullable = false)
    private MainTask mainTask;  // Hlavní úkol, ke kterému vedlejší úkol patří

    private String title;  // Název vedlejšího úkolu
    private String description;  // Popis vedlejšího úkolu

    private boolean completed;  // Stav dokončení vedlejšího úkolu
    @OneToMany(mappedBy = "subTask", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubTaskRole> subTaskRoles;

}
