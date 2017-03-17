package ru.javaops.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.javaops.model.Group;

import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public interface GroupRepository extends JpaRepository<Group, Integer> {

    @Query("SELECT DISTINCT(ug.group) FROM UserGroup ug " +
            " WHERE ug.user.id = :userId")
    Set<Group> findByUser(@Param("userId") int userId);

    @Override
    @Query("SELECT g FROM Group g JOIN FETCH g.project")
    List<Group> findAll();
}