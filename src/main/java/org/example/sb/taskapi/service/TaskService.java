package org.example.sb.taskapi.service;

import org.example.sb.taskapi.domain.Task;
import org.example.sb.taskapi.dto.TaskRequest;
import org.example.sb.taskapi.dto.TaskResponse;
import org.example.sb.taskapi.repository.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public TaskResponse create(TaskRequest request) {
        validate(request);

        Task task = new Task(
                null,
                request.title(),
                request.description(),
                request.dueDate(),
                request.priority(),
                request.completed(),
                request.category()
        );

        return toResponse(repository.save(task));
    }

    public List<TaskResponse> findAll(Boolean completed) {
        List<Task> tasks = repository.findAll();

        if (completed != null) {
            tasks = tasks.stream()
                    .filter(task -> task.getCompleted().equals(completed))
                    .toList();
        }

        return tasks.stream()
                .map(this::toResponse)
                .toList();
    }

    public TaskResponse findById(Long id) {
        return toResponse(findTask(id));
    }

    public TaskResponse update(Long id, TaskRequest request) {
        validate(request);

        Task task = findTask(id);
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDueDate(request.dueDate());
        task.setPriority(request.priority());
        task.setCompleted(request.completed());
        task.setCategory(request.category());

        return toResponse(repository.update(task));
    }

    public void delete(Long id) {
        findTask(id);
        repository.deleteById(id);
    }

    private Task findTask(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found: " + id
                ));
    }

    private void validate(TaskRequest request) {
        if (request.title() == null || request.title().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Title is required"
            );
        }

        if (request.priority() == null
                || request.priority() < 1
                || request.priority() > 5) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Priority must be between 1 and 5"
            );
        }
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getPriority(),
                task.getCompleted(),
                task.getCategory()
        );
    }
}