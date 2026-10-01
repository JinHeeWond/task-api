package org.example.sb.taskapi.repository;

import org.example.sb.taskapi.domain.Task;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class MemoryTaskRepository implements TaskRepository {

    private final Map<Long, Task> store = new LinkedHashMap<>();
    private long sequence = 0L;

    @Override
    public Task save(Task task) {
        task.setId(++sequence);
        store.put(task.getId(), task);
        return task;
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Task update(Task task) {
        store.put(task.getId(), task);
        return task;
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }
}