package ru.javaops.service;

import ru.javaops.model.User;
import ru.javaops.to.UserToExt;

import java.util.Collection;
import java.util.Set;

/**
 * GKislin
 * 13.02.2016
 */
public interface UserService {

    void deleteByEmail(String email);

    Collection<User> getGroup(String groupName);

    User findExistedByEmail(String email);

    User findByEmail(String email);

    Set<User> findByGroupName(String email);

    User findByEmailAndGroupId(String email, int groupId);

    void save(User u);

    void update(UserToExt userTo);
}
