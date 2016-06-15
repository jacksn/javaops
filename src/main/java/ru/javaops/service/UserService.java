package ru.javaops.service;

import ru.javaops.model.RegisterType;
import ru.javaops.model.User;
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

    Set<User> findByGroupNameAndRegisterType(String groupName, RegisterType registerType);

    User findByEmailAndGroupId(String email, int groupId);

    void save(User u);

    void update(UserToExt userTo);

    Set<User> findByLocation(String location);
}
