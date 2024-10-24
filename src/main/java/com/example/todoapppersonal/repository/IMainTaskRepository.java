package com.example.todoapppersonal.repository;

import com.example.todoapppersonal.model.MainTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMainTaskRepository extends JpaRepository<MainTask,Long> {
}
