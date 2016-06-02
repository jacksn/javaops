package ru.javaops.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import ru.javaops.model.Project;
import ru.javaops.repository.ProjectRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * GKislin
 * 15.02.2016
 */
@Service
public class ProjectService {
    private final Logger log = LoggerFactory.getLogger(GroupService.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CacheManager cacheManager;

    public Project findByName(String name) {
        Project project = projectRepository.findByName(name);
        checkNotNull(project, "Не найден проект '" + name + '\'');
        return project;
    }

    public List<Project> getAll() {
        log.debug("getAll");
        List<Project> projects = projectRepository.findAll();
        Cache cache = cacheManager.getCache("project");
        projects.forEach(p -> cache.put(p.getName(), p));
        return projects;
    }
}
