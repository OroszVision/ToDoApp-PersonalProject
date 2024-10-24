package com.example.todoapppersonal.repository;

import com.example.todoapppersonal.model.MainTask;
import com.example.todoapppersonal.model.TaskRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ITaskRoleRepository extends JpaRepository<TaskRole,Long> {
    @Query("SELECT tr.mainTask FROM TaskRole tr WHERE tr.user.id = :userId AND (tr.role = 'OWNER' OR tr.role = 'COLLABORATOR')")
    List<MainTask> findMainTasksByUserAndRole(@Param("userId") Long userId);

    @Query("SELECT tr.mainTask from TaskRole tr WHERE tr.user.id = :userId AND tr.mainTask.Id = :taskId")
    MainTask findMainTasksByUserAndTask(Long userId, Long taskId);
    List<TaskRole> findByUserIdAndMainTask_Id(Long userId, Long taskId);
}

