package org.example.sb.taskapi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void createTask() throws Exception {
        String requestJson = """
            {
              "title": "테스트 할 일",
              "description": "POST 등록 테스트",
              "dueDate": "2026-10-20",
              "priority": 3,
              "completed": false,
              "category": "테스트"
            }
            """;

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("테스트 할 일"));
    }

    @Test
    void findAllTasks() throws Exception {
        String requestJson = """
            {
              "title": "조회 테스트",
              "description": "전체 조회 테스트",
              "dueDate": "2026-10-21",
              "priority": 2,
              "completed": false,
              "category": "테스트"
            }
            """;

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("조회 테스트"));
    }

    @Test
    void findTaskById() throws Exception {
        String requestJson = """
            {
              "title": "단건 조회 테스트",
              "description": "id로 조회",
              "dueDate": "2026-10-22",
              "priority": 4,
              "completed": false,
              "category": "테스트"
            }
            """;

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("단건 조회 테스트"));
    }

    @Test
    void updateTask() throws Exception {
        String createJson = """
            {
              "title": "수정 전 제목",
              "description": "수정 전 설명",
              "dueDate": "2026-10-23",
              "priority": 2,
              "completed": false,
              "category": "테스트"
            }
            """;

        String updateJson = """
            {
              "title": "수정 후 제목",
              "description": "수정 후 설명",
              "dueDate": "2026-10-24",
              "priority": 5,
              "completed": true,
              "category": "백엔드"
            }
            """;

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정 후 제목"))
                .andExpect(jsonPath("$.completed").value(true));
    }
}