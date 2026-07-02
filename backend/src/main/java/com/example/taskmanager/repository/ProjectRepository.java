package com.example.taskmanager.repository;

import com.example.taskmanager.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    // projects the user owns OR is a member of
    List<Project> findByOwner_IdOrMembers_Id(Long ownerId, Long memberId);
}
