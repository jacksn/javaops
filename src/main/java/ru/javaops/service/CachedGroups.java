package ru.javaops.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.javaops.model.Group;
import ru.javaops.repository.GroupRepository;

import java.util.List;

/**
 * gkislin
 * 17.03.2017
 */
@Service
@Slf4j
public class CachedGroups {
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    GroupRepository groupRepository;

    @Cacheable("groups")
    public List<Group> getAll() {
        log.debug("getAll");
        List<Group> groups = groupRepository.findAll();
        Cache cache = cacheManager.getCache("group");
        groups.forEach(g -> cache.put(g.getName(), g));
        return groups;
    }

    @Cacheable("group")
    public Group findByName(String name) {
        List<Group> groups = getAll();
        return groups.stream()
                .filter(g -> name.equals(g.getName()))
                .findAny().orElseThrow(() -> new IllegalArgumentException("Не найдена группа '" + name + '\''));
    }
}
