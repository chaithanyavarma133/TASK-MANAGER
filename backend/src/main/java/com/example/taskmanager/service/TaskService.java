package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskStatusUpdateRequest;
import com.example.taskmanager.model.Project;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectService projectService;

    public List<Task> getTasksForProject(Long projectId, String username) {
        projectService.getAccessibleProject(projectId, username); // throws if no access
        return taskRepository.findByProject_Id(projectId);
    }

    public Task createTask(Long projectId, TaskRequest request, String username) {
        Project project = projectService.getAccessibleProject(projectId, username);

        Task.TaskBuilder builder = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .project(project);

        if (request.getStatus() != null) {
            builder.status(request.getStatus());
        }
        if (request.getAssigneeId() != null) {
            builder.assignee(resolveAssignee(request.getAssigneeId()));
        }

        return taskRepository.save(builder.build());
    }

    public Task updateTask(Long taskId, TaskRequest request, String username) {
        Task task = getAccessibleTask(taskId, username);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getAssigneeId() != null) {
            task.setAssignee(resolveAssignee(request.getAssigneeId()));
        }

        return taskRepository.save(task);
    }

    public Task updateStatus(Long taskId, TaskStatusUpdateRequest request, String username) {
        Task task = getAccessibleTask(taskId, username);
        task.setStatus(request.getStatus());
        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId, String username) {
        Task task = getAccessibleTask(taskId, username);
        taskRepository.delete(task);
    }

    public Task getAccessibleTask(Long taskId, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        // will throw 403/404 if the user cannot access the parent project
        projectService.getAccessibleProject(task.getProject().getId(), username);
        return task;
    }

    private User resolveAssignee(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignee not found"));
    }
}
