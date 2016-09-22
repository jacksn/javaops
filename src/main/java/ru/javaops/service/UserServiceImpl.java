package ru.javaops.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javaops.LoggedUser;
import ru.javaops.model.GroupType;
import ru.javaops.model.RegisterType;
import ru.javaops.model.User;
import ru.javaops.repository.UserRepository;
import ru.javaops.to.UserStat;
import ru.javaops.to.UserToExt;
import ru.javaops.util.UserUtil;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Authenticate a user from the database.
 */
@Service("userDetailsService")
public class UserServiceImpl implements UserService, org.springframework.security.core.userdetails.UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public LoggedUser loadUserByUsername(final String email) {
        String lowercaseLogin = email.toLowerCase();
        log.debug("Authenticating {}", email);
        User user = userRepository.findByEmail(lowercaseLogin);
        if (user == null) {
            throw new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the database");
        }
        if (!user.isActive()) {
            throw new DisabledException("User " + lowercaseLogin + " was not activated");
        }
        return new LoggedUser(user);
    }


    @Override
    @Transactional
    public void deleteByEmail(String email) {
        log.debug("Delete user " + email);
        User user = userRepository.findByEmail(email);
        if (user != null) {
            userRepository.delete(user);
        } else {
            log.warn("User " + email + " is not found");
        }
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase());
    }

    @Override
    public User findExistedByEmail(String email) {
        return checkNotNull(findByEmail(email), "Пользователь %s не найден", email);
    }

    @Override
    public Set<User> findByGroupName(String groupName) {
        return userRepository.findByGroupName(groupName);
    }

    @Override
    public Set<User> findByGroupTypes(GroupType[] groupTypes) {
        return userRepository.findByGroupType(groupTypes);
    }

    @Override
    public Set<User> findByGroupNameAndRegisterType(String groupName, RegisterType registerType) {
        return userRepository.findByGroupNameAndRegisterType(groupName, registerType);
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
    public User findByEmailAndProjectId(String email, int projectId) {
        return userRepository.findByEmailAndProjectId(email.toLowerCase(), projectId);
    }

    @Override
    @Transactional
    public User update(UserToExt userTo) {
        User user = userRepository.findOne(userTo.getId());
        UserUtil.updateFromToExt(user, userTo);
        userRepository.save(user);
        return user;
    }

    @Override
    public Set<User> findByLocation(String location) {
        return userRepository.findByLocation(location.toLowerCase());
    }

    @Override
    public List<UserStat> findAllForStats() {
        return userRepository.findAllForStats();
    }
}
