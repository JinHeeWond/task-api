package org.example.sb.taskapi.dto;

import java.time.LocalDate;

public record TaskRequest(
        String title,
        String description,
        LocalDate dueDate,
        Integer priority,
        Boolean completed,
        String category
) {
}