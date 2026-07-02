package com.example.taskmanager.controller;

import com.example.taskmanager.dto.CommentRequest;
import com.example.taskmanager.model.Comment;
import com.example.taskmanager.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long taskId, Authentication auth) {
        return ResponseEntity.ok(commentService.getCommentsForTask(taskId, auth.getName()));
    }

    @PostMapping
    public ResponseEntity<Comment> addComment(@PathVariable Long taskId,
                                               @Valid @RequestBody CommentRequest request,
                                               Authentication auth) {
        return ResponseEntity.ok(commentService.addComment(taskId, request, auth.getName()));
    }
}
