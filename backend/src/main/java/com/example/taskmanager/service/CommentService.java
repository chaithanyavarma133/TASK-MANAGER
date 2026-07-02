package com.example.taskmanager.service;

import com.example.taskmanager.dto.CommentRequest;
import com.example.taskmanager.model.Comment;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.CommentRepository;
import com.example.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TaskService taskService;

    public List<Comment> getCommentsForTask(Long taskId, String username) {
        taskService.getAccessibleTask(taskId, username);
        return commentRepository.findByTask_IdOrderByCreatedAtAsc(taskId);
    }

    public Comment addComment(Long taskId, CommentRequest request, String username) {
        Task task = taskService.getAccessibleTask(taskId, username);
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .task(task)
                .author(author)
                .build();

        return commentRepository.save(comment);
    }
}
