package ru.javaops.service;

import ru.javaops.model.GroupType;
import ru.javaops.model.RegisterType;
import ru.javaops.model.User;
import ru.javaops.to.UserStat;
import ru.javaops.to.UserToExt;

import java.util.Set;

/**
 * GKislin
 * 13.02.2016
 */
public interface UserService {

    void deleteByEmail(String email);

    User findExistedByEmail(String email);

    User findByEmail(String email);

    Set<User> findByGroupName(String email);

    Set<User> findByGroupTypes(GroupType[] groupTypes);

    Set<User> findByGroupNameAndRegisterType(String groupName, RegisterType registerType);

    User findByEmailAndGroupId(String email, int groupId);

    User findByEmailAndProjectId(String email, int projectId);

    void save(User u);

    User update(UserToExt userTo);

    Set<User> findByLocation(String location);

    Set<UserStat> findAllForStats();
}
