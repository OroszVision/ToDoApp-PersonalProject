package com.example.todoapppersonal.repository;

import com.example.todoapppersonal.model.SubTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISubTaskRepository extends JpaRepository<SubTask,Long> {
}
