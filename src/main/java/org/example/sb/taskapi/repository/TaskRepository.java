package org.example.sb.taskapi.repository;

import org.example.sb.taskapi.domain.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    Task save(Task task);

    List<Task> findAll();

    Optional<Task> findById(Long id);

    Task update(Task task);

    void deleteById(Long id);
}