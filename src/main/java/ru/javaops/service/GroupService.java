package ru.javaops.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javaops.model.*;
import ru.javaops.repository.GroupRepository;
import ru.javaops.repository.PaymentRepository;
import ru.javaops.repository.UserGroupRepository;
import ru.javaops.to.UserMail;
import ru.javaops.to.UserTo;
import ru.javaops.util.ProjectUtil;
import ru.javaops.util.ProjectUtil.ProjectProps;
import ru.javaops.util.TimeUtil;
import ru.javaops.util.UserUtil;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * GKislin
 * 15.02.2016
 */
@Service
public class GroupService {
    private final Logger log = LoggerFactory.getLogger(GroupService.class);

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private CachedGroups cachedGroups;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentRepository paymentRepository;

    public boolean isProjectMember(int userId, String projectName) {
        return getGroupsByUserId(userId).stream()
                .anyMatch(g -> g.isMembers() && projectName.equals(g.getProject().getName()));
    }

    public Set<Group> getGroupsByUserId(int userId) {
        log.debug("getGroupsByUserId {}", userId);
        return groupRepository.findByUser(userId);
    }

    @Transactional
    public UserGroup registerAtProject(UserTo userTo, String projectName, String channel) {
        log.info("add{} to project {}", userTo, projectName);

        ProjectProps projectProps = getProjectProps(projectName);
        return registerAtGroup(userTo, channel, projectProps.registeredGroup, null,
                user -> {
                    RegisterType registerType = isProjectMember(user.getId(), projectProps.project.getName()) ?
                            RegisterType.REPEAT : RegisterType.REGISTERED;

                    return new UserGroup(user,
                            registerType == RegisterType.REGISTERED ? projectProps.registeredGroup : projectProps.currentGroup,
                            registerType, channel);
                });
    }

    @Transactional
    public UserGroup registerAtGroup(UserTo userTo, String groupName, String channel, ParticipationType participationType) {
        log.info("add{} to group {}", userTo, groupName);
        Group group = cachedGroups.findByName(groupName);
        return registerAtGroup(userTo, channel, group, participationType,
                user -> new UserGroup(user, group, RegisterType.REGISTERED, channel));
    }

    @Transactional
    public UserGroup pay(UserTo userTo, String groupName, Payment payment, ParticipationType participationType, String channel) {
        log.info("Pay from {} for {}: {}", userTo, groupName, payment);
        Group group = cachedGroups.findByName(groupName);
        UserGroup ug = registerAtGroup(userTo, channel, group, participationType,
                user -> new UserGroup(user, group, RegisterType.REGISTERED, channel));
        payment.setUserGroup(ug);
        paymentRepository.save(payment);
        return ug;
    }

    private UserGroup registerAtGroup(UserTo userTo, String channel, Group newUserGroup, ParticipationType type, Function<User, UserGroup> existedUserGroupProvider) {
        User user = userService.findByEmail(userTo.getEmail());
        UserGroup ug;
        if (user == null) {
            user = UserUtil.createFromTo(userTo);
            ug = new UserGroup(user, newUserGroup, RegisterType.FIRST_REGISTERED, channel);
        } else {
            ug = existedUserGroupProvider.apply(user);
            UserGroup oldUserGroup = userGroupRepository.findByUserIdAndGroupId(user.getId(), ug.getGroup().getId());
            if (oldUserGroup != null) {
                oldUserGroup.setAlreadyExist(true);
                if (Objects.equals(oldUserGroup.getParticipationType(), type)) {
                    return oldUserGroup;
                }
                oldUserGroup.setParticipationType(type);
                return userGroupRepository.save(oldUserGroup);
            }
        }
        Group group = ug.getGroup();
        if (ug.getRegisterType() != RegisterType.REPEAT) {
            if (group.isMembers() && ug.getRegisterType() == RegisterType.REGISTERED) {
                ug = checkRemoveFromRegistered(ug);
            }
            if (group.getRole() != null) {
                user.getRoles().add(group.getRole());
            }
            userService.save(user);
        }
        ug.setParticipationType(type);
        return userGroupRepository.save(ug);
    }

    public UserGroup save(UserGroup userGroup) {
        return userGroupRepository.save(userGroup);
    }

    private UserGroup checkRemoveFromRegistered(UserGroup ug) {
        ProjectProps projectProps = getProjectProps(ug.getGroup().getProject().getName());
        UserGroup registeredUserGroup = userGroupRepository.findByUserIdAndGroupId(ug.getUser().getId(), projectProps.registeredGroup.getId());
        if (registeredUserGroup == null) {
            return ug;
        }
        registeredUserGroup.setRegisterType(RegisterType.REGISTERED);
        registeredUserGroup.setGroup(ug.getGroup());
        if (registeredUserGroup.getChannel() == null) {
            registeredUserGroup.setChannel(ug.getChannel());
        }
        registeredUserGroup.setRegisteredDate(new Date());
        return registeredUserGroup;
    }

    public ProjectProps getProjectProps(String projectName) {
        return ProjectUtil.getProjectProps(projectName, cachedGroups.getAll());
    }

    public Set<UserMail> filterUserByGroupNames(String includes, String excludes, LocalDate startRegisteredDate, LocalDate endRegisteredDate) {
        final List<Group> groups = cachedGroups.getAll();
        final Set<UserMail> includeUsers = filterUserByGroupNames(groups, includes, startRegisteredDate, endRegisteredDate);
        if (StringUtils.isNoneEmpty(excludes)) {
            Set<UserMail> excludeUsers = filterUserByGroupNames(groups, excludes, startRegisteredDate, endRegisteredDate);
            includeUsers.removeAll(excludeUsers);
        }
        return includeUsers;
    }

    public User getExistedUserInCurrentProject(String email, String projectName) {
        User u;
        if (projectName.equals("javaops")) {
            u = userService.findExistedByEmail(email);
            if (!u.isMember()) {
                throw new IllegalStateException("Регистрация только для участников Java Online Projects");
            }
        } else {
            ProjectProps projectProps = getProjectProps(projectName);
            u = userService.findByEmailAndGroupId(email, projectProps.currentGroup.getId());
            checkNotNull(u, "Пользователь %s не найден в группе %s", email, projectProps.currentGroup.getName());
        }
        return u;
    }

    private Set<UserMail> filterUserByGroupNames(List<Group> groups, String groupNames, LocalDate startRegisteredDate, LocalDate endRegisteredDate) {
        List<Predicate<String>> predicates = getMatcher(groupNames);
        Date startDate = TimeUtil.toDate(startRegisteredDate);
        Date endDate = TimeUtil.toDate(endRegisteredDate);

        // filter users by predicates
        return groups.stream().filter(group -> predicates.stream().anyMatch(p -> p.test(group.getName())))
                .flatMap(group -> userService.findByGroupName(group.getName()).stream())
                .filter(um -> startDate == null || um.getRegisteredDate().compareTo(startDate) >= 0)
                .filter(um -> endDate == null || um.getRegisteredDate().compareTo(endDate) <= 0)
                .collect(Collectors.toSet());
    }

    // group matches predicates
    private List<Predicate<String>> getMatcher(String groupNames) {
        return Arrays.stream(groupNames.split(","))
                .map(String::trim)
                .map(paramName -> paramName.charAt(paramName.length() - 1) == '*' ?
                        new Predicate<String>() {
                            final String startWith = StringUtils.chop(paramName);

                            @Override
                            public boolean test(String name) {
                                return name.startsWith(startWith);
                            }
                        } :
                        (Predicate<String>) paramName::equals
                ).collect(Collectors.toList());
    }
}
