package ru.javaops.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javaops.AuthorizedUser;
import ru.javaops.model.User;
import ru.javaops.repository.UserRepository;
import ru.javaops.to.UserMail;
import ru.javaops.to.UserToExt;
import ru.javaops.util.UserUtil;

import java.util.Set;

/**
 * Authenticate a user from the database.
 */
@Service("userDetailsService")
public class UserServiceImpl implements UserService, org.springframework.security.core.userdetails.UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public AuthorizedUser loadUserByUsername(final String email) {
        String lowerEmail = email.toLowerCase();
        log.info("Authenticating {}", lowerEmail);
        return new AuthorizedUser(findExistedByEmail(lowerEmail));
    }

    @Override
    @Transactional
    public boolean deleteByEmail(String email) {
        log.debug("Delete user " + email);
        User user = userRepository.findByEmail(email);
        if (user != null) {
            userRepository.delete(user);
            return true;
        } else {
            log.warn("User " + email + " is not found");
            return false;
        }
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase());
    }

    @Override
    public User findByEmailOrGmail(String email) {
        return userRepository.findByEmailOrGmail(email.toLowerCase());
    }

    @Override
    public User findExistedByEmail(String email) {
        return checkExist(findByEmail(email), email);
    }

    private User checkExist(User user, String email) {
        if (user == null) {
            throw new UsernameNotFoundException("Пользователь c почтой <b>" + email + "</b> не найден");
        }
        return user;
    }

    @Override
    public Set<UserMail> findByGroupName(String groupName) {
        return userRepository.findByGroupName(groupName);
    }

    @Override
    public void save(User u) {
        userRepository.save(u);
    }

    @Override
    public User findByEmailAndGroupId(String email, int groupId) {
        return userRepository.findByEmailAndGroupId(email.toLowerCase(), groupId);
    }

    @Override
    @Transactional
    public User update(UserToExt userTo) {
        User user = userRepository.findOne(userTo.getId());
        UserUtil.updateFromToExt(user, userTo);
        return userRepository.save(user);
    }

    @Override
    public User findByEmailAndGroupName(String email, String groupName) {
        return userRepository.findByEmailAndGroupName(email, groupName);
    }

    @Override
    public User get(int id) {
        return userRepository.findOne(id);
    }
}
