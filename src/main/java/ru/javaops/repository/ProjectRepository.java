package ru.javaops.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.javaops.model.Project;

@Transactional(readOnly = true)
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    Project getByName(String name);
}