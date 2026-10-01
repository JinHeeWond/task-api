package org.example.sb.taskapi.dto;

import java.time.LocalDate;

public record TaskResponse(
        Long id,
        String title,
        String description,
        LocalDate dueDate,
        Integer priority,
        Boolean completed,
        String category
) {
}