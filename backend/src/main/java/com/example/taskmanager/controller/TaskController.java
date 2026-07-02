package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskStatusUpdateRequest;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/api/projects/{projectId}/tasks")
    public ResponseEntity<List<Task>> getTasks(@PathVariable Long projectId, Authentication auth) {
        return ResponseEntity.ok(taskService.getTasksForProject(projectId, auth.getName()));
    }

    @PostMapping("/api/projects/{projectId}/tasks")
    public ResponseEntity<Task> createTask(@PathVariable Long projectId,
                                            @Valid @RequestBody TaskRequest request,
                                            Authentication auth) {
        return ResponseEntity.ok(taskService.createTask(projectId, request, auth.getName()));
    }

    @PutMapping("/api/tasks/{taskId}")
    public ResponseEntity<Task> updateTask(@PathVariable Long taskId,
                                            @Valid @RequestBody TaskRequest request,
                                            Authentication auth) {
        return ResponseEntity.ok(taskService.updateTask(taskId, request, auth.getName()));
    }

    @PatchMapping("/api/tasks/{taskId}/status")
    public ResponseEntity<Task> updateStatus(@PathVariable Long taskId,
                                              @Valid @RequestBody TaskStatusUpdateRequest request,
                                              Authentication auth) {
        return ResponseEntity.ok(taskService.updateStatus(taskId, request, auth.getName()));
    }

    @DeleteMapping("/api/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId, Authentication auth) {
        taskService.deleteTask(taskId, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
