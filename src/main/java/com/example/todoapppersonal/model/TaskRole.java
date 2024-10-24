    package com.example.todoapppersonal.model;

    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    @Entity
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public class TaskRole {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false)
        private AppUser user;  // Uživatel, který má roli

        @ManyToOne
        @JoinColumn(name = "task_id", nullable = false)
        private MainTask mainTask;  // Hlavní úkol, ke kterému se role vztahuje

        @Enumerated(EnumType.STRING)
        private Role role;  // Role uživatele (majitel, kolaborant)
    }