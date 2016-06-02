package ru.javaops.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.javaops.model.Project;

import java.util.List;

@Transactional(readOnly = true)
public interface ProjectRepository extends JpaRepository<Project, Integer> {

    @Query("SELECT p FROM Project p WHERE p.name = :name")
    @Cacheable("project")
    Project findByName(@Param("name") String name);

    @Cacheable("projects")
    List<Project> findAll();
}