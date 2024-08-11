package com.artur.task_management_system.repository;

import com.artur.task_management_system.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findAllByAuthorId(Long authorId, Pageable pageable);

    @Query("SELECT t FROM Task t JOIN t.performers p WHERE p.id = :performerId")
    Page<Task> findAllByPerformerId(@Param("performerId") Long performerId, Pageable pageable);
}
