package ru.javaops.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.javaops.model.GroupType;
import ru.javaops.model.RegisterType;
import ru.javaops.model.User;
import ru.javaops.to.UserStat;

import java.util.Set;

@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u " +
            "  LEFT JOIN FETCH u.roles WHERE u.email=:email")
    User findByEmail(@Param("email") String email);

    @Query("SELECT DISTINCT(ug.user) FROM UserGroup ug " +
            " WHERE ug.group.name=:groupName AND ug.user.active=TRUE")
    Set<User> findByGroupName(@Param("groupName") String groupName);

    @Query("SELECT DISTINCT(ug.user) FROM UserGroup ug " +
            " WHERE ug.group.type IN (:groupTypes) AND ug.user.active=TRUE")
    Set<User> findByGroupType(@Param("groupTypes") GroupType[] groupTypes);

    @Query("SELECT DISTINCT(ug.user) FROM UserGroup ug " +
            " WHERE ug.registerType=:registerType AND ug.group.name=:groupName AND ug.user.active=TRUE")
    Set<User> findByGroupNameAndRegisterType(@Param("groupName") String groupName, @Param("registerType") RegisterType registerType);

    @Query("SELECT ug.user FROM UserGroup ug WHERE ug.user.email=:email AND ug.group.project.id=:projectId")
    User findByEmailAndProjectId(@Param("email") String email, @Param("projectId") int projectId);

    @Query("SELECT ug.user FROM UserGroup ug WHERE ug.user.email=:email AND ug.group.id=:groupId")
    User findByEmailAndGroupId(@Param("email") String email, @Param("groupId") int groupId);

    @Query("SELECT u FROM User u WHERE LOWER(u.location) LIKE CONCAT('%', :location, '%')")
    Set<User> findByLocation(@Param("location") String location);

    @Query("SELECT new ru.javaops.to.UserStat(u.fullName, u.email, u.location, u.aboutMe, u.skype) FROM User u WHERE u.statsAgree=TRUE ORDER BY u.location")
    Set<UserStat> findAllForStats();
}