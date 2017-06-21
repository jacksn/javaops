package ru.javaops.service;

import ru.javaops.model.User;
import ru.javaops.to.UserMail;
import ru.javaops.to.UserToExt;

import java.util.Set;

/**
 * GKislin
 * 13.02.2016
 */
public interface UserService {

    boolean deleteByEmail(String email);

    User findExistedByEmail(String email);

    User findByEmail(String email);

    Set<UserMail> findByGroupName(String email);

    User findByEmailAndGroupId(String email, int groupId);

    void save(User u);

    User update(UserToExt userTo);

    User findByEmailAndGroupName(String email, String groupName);

    User get(int id);
}