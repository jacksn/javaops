package ru.javaops.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.javaops.model.RegisterType;
import ru.javaops.model.User;

import java.util.Set;

@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u " +
            " LEFT JOIN u.roles WHERE u.email=:email")
    User findByEmail(@Param("email") String email);

    @Query("SELECT DISTINCT(ug.user) FROM UserGroup ug " +
            " WHERE ug.group.name=:groupName AND ug.user.active=TRUE")
    Set<User> findByGroupName(@Param("groupName") String groupName);

    @Query("SELECT DISTINCT(ug.user) FROM UserGroup ug " +
            " WHERE ug.registerType=:registerType AND ug.group.name=:groupName AND ug.user.active=TRUE")
    Set<User> findByGroupNameAndRegisterType(@Param("groupName") String groupName, @Param("registerType") RegisterType registerType);

    @Query("SELECT ug.user FROM UserGroup ug WHERE ug.user.email=:email AND ug.group.id=:groupId")
    User findByEmailAndGroupId(@Param("email") String email, @Param("groupId") int groupId);

}