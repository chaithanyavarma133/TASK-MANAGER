package com.example.taskmanager.service;

import com.example.taskmanager.dto.AddMemberRequest;
import com.example.taskmanager.dto.ProjectRequest;
import com.example.taskmanager.model.Project;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.ProjectRepository;
import com.example.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public Project createProject(ProjectRequest request, String ownerUsername) {
        User owner = getUser(ownerUsername);

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(owner)
                .build();

        return projectRepository.save(project);
    }

    public List<Project> getProjectsForUser(String username) {
        User user = getUser(username);
        return projectRepository.findByOwner_IdOrMembers_Id(user.getId(), user.getId());
    }

    public Project getAccessibleProject(Long projectId, String username) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        User user = getUser(username);
        boolean isOwner = project.getOwner().getId().equals(user.getId());
        boolean isMember = project.getMembers().stream().anyMatch(m -> m.getId().equals(user.getId()));

        if (!isOwner && !isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this project");
        }
        return project;
    }

    public Project addMember(Long projectId, AddMemberRequest request, String requesterUsername) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        if (!project.getOwner().getUsername().equals(requesterUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the project owner can add members");
        }

        User newMember = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + request.getUsername()));

        project.getMembers().add(newMember);
        return projectRepository.save(project);
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
