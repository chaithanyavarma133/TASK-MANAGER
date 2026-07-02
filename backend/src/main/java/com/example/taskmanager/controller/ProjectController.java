package com.example.taskmanager.controller;

import com.example.taskmanager.dto.AddMemberRequest;
import com.example.taskmanager.dto.ProjectRequest;
import com.example.taskmanager.model.Project;
import com.example.taskmanager.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<Project>> getMyProjects(Authentication auth) {
        return ResponseEntity.ok(projectService.getProjectsForUser(auth.getName()));
    }

    @PostMapping
    public ResponseEntity<Project> createProject(@Valid @RequestBody ProjectRequest request, Authentication auth) {
        return ResponseEntity.ok(projectService.createProject(request, auth.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProject(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(projectService.getAccessibleProject(id, auth.getName()));
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<Project> addMember(@PathVariable Long id,
                                              @Valid @RequestBody AddMemberRequest request,
                                              Authentication auth) {
        return ResponseEntity.ok(projectService.addMember(id, request, auth.getName()));
    }
}
