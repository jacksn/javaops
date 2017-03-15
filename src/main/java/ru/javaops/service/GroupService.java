package ru.javaops.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.javaops.model.*;
import ru.javaops.repository.GroupRepository;
import ru.javaops.repository.PaymentRepository;
import ru.javaops.repository.UserGroupRepository;
import ru.javaops.to.UserTo;
import ru.javaops.util.ProjectUtil;
import ru.javaops.util.ProjectUtil.ProjectProps;
import ru.javaops.util.TimeUtil;
import ru.javaops.util.UserUtil;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
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
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CacheManager cacheManager;

    public Group findByName(String name) {
        Group group = groupRepository.findByName(name);
        checkNotNull(group, "Не найдена группа '" + name + '\'');
        return group;
    }

    public List<Group> getAll() {
        log.debug("getAll");
        List<Group> groups = groupRepository.findAll(new Sort("name"));
        Cache cache = cacheManager.getCache("group");
        groups.forEach(g -> cache.put(g.getName(), g));
        return groups;
    }

    public Set<Group> findByUserId(int userId) {
        log.debug("findByUserId {}", userId);
        return groupRepository.findByUser(userId);
    }

    @Transactional
    public UserGroup registerAtProject(UserTo userTo, String projectName, String channel) {
        log.info("add{} to project {}", userTo, projectName);

        ProjectProps projectProps = getProjectProps(projectName);
        return registerAtGroup(userTo, channel, projectProps.registeredGroup,
                user -> {
                    Set<Group> groups = findByUserId(user.getId());
                    RegisterType registerType = groups.stream()
                            .anyMatch(g -> projectProps.project.equals(g.getProject()) && g.isMembers()) ? RegisterType.REPEAT : RegisterType.REGISTERED;
                    return new UserGroup(user,
                            registerType == RegisterType.REGISTERED ? projectProps.registeredGroup : projectProps.currentGroup,
                            registerType, channel);
                });
    }

    @Transactional
    public UserGroup registerAtGroup(UserTo userTo, String groupName, String channel) {
        log.info("add{} to group {}", userTo, groupName);
        Group group = findByName(groupName);
        return registerAtGroup(userTo, channel, group,
                user -> new UserGroup(user, group, RegisterType.REGISTERED, channel));
    }

    @Transactional
    public UserGroup pay(UserTo userTo, String projectName, Payment payment, ParticipationType participationType, String channel) {
        log.info("Pay from {} for {}: {}", userTo, projectName, payment);
        ProjectProps projectProps = getProjectProps(projectName);
        UserGroup ug = registerAtGroup(userTo, channel, projectProps.currentGroup,
                user -> new UserGroup(user, projectProps.currentGroup, RegisterType.REGISTERED, null));

        paymentRepository.save(payment);
        ug.setPayment(payment);
        ug.setParticipationType(participationType);
        userGroupRepository.save(ug);
        return ug;
    }

    private UserGroup registerAtGroup(UserTo userTo, String channel, Group newUserGroup, Function<User, UserGroup> existedUserGroupProvider) {
        User user = userService.findByEmail(userTo.getEmail());
        UserGroup ug;
        if (user == null) {
            user = UserUtil.createFromTo(userTo);
            ug = new UserGroup(user, newUserGroup, RegisterType.FIRST_REGISTERED, channel);
        } else {
            ug = existedUserGroupProvider.apply(user);
            if (userGroupRepository.findByUserIdAndGroupId(user.getId(), ug.getGroup().getId()) != null) {
                ug.setRegisterType(RegisterType.DUPLICATED);
                return ug;
            }
        }
        if (ug.getRegisterType() != RegisterType.REPEAT) {
            if (ug.getGroup().isMembers()) {
                if (ug.getRegisterType() == RegisterType.REGISTERED) {
                    ug = checkRemoveFromRegistered(ug);
                }
                user.getRoles().add(Role.ROLE_MEMBER);
            }
            userService.save(user);
        }
        return userGroupRepository.save(ug);
    }

    public UserGroup save(User user, Group group, RegisterType registerType, String channel) {
        return userGroupRepository.save(new UserGroup(user, group, registerType, channel));
    }

    private UserGroup checkRemoveFromRegistered(UserGroup ug) {
        ProjectProps projectProps = getProjectProps(ug.getGroup().getProject().getName());
        UserGroup registeredUserGroup = userGroupRepository.findByUserIdAndGroupId(ug.getUser().getId(), projectProps.registeredGroup.getId());
        if (registeredUserGroup == null) {
            return ug;
        }
        registeredUserGroup.setRegisterType(RegisterType.REGISTERED);
        registeredUserGroup.setGroup(ug.getGroup());
        if (ug.getChannel() != null) {
            registeredUserGroup.setChannel(ug.getChannel());
        }
        registeredUserGroup.setRegisteredDate(new Date());
        return registeredUserGroup;
    }

    public ProjectProps getProjectProps(String projectName) {
        return ProjectUtil.getProjectProps(projectName, getAll());
    }

    public Set<User> filterUserByGroupNames(String includes, String excludes, RegisterType registerType, GroupType[] groupTypes, LocalDate startRegisteredDate, LocalDate endRegisteredDate) {
        final List<Group> groups = getAll();
        final Set<User> includeUsers = (groupTypes == null || groupTypes.length == 0) ?
                filterUserByGroupNames(groups, includes, registerType, startRegisteredDate, endRegisteredDate) :
                userService.findByGroupTypes(groupTypes);

        if (StringUtils.isNoneEmpty(excludes)) {
            Set<User> excludeUsers = filterUserByGroupNames(groups, excludes, null, startRegisteredDate, endRegisteredDate);
            includeUsers.removeAll(excludeUsers);
        }
        return includeUsers;
    }

    public User getUserInProject(String email, String projectName) {
        User u;
        if (projectName.equals("javaops")) {
            u = userService.findByEmail(email);
            checkNotNull(u, "Пользователь %s не найден в проекте %s", email, projectName);
            if (CollectionUtils.isEmpty(u.getRoles())) {
                throw new IllegalStateException("Регистрация только для участников Java Online Projects");
            }
        } else {
            ProjectProps projectProps = getProjectProps(projectName);
            u = userService.findByEmailAndGroupId(email, projectProps.currentGroup.getId());
            checkNotNull(u, "Пользователь %s не найден в проекте %s", email, projectName);
        }
        return u;
    }

    private Set<User> filterUserByGroupNames(List<Group> groups, String groupNames, RegisterType registerType, LocalDate startRegisteredDate, LocalDate endRegisteredDate) {
        List<Predicate<String>> predicates = getMatcher(groupNames);
        Date startDate = TimeUtil.toDate(startRegisteredDate);
        Date endDate = TimeUtil.toDate(endRegisteredDate);

        // filter users by predicates
        return groups.stream().filter(group -> predicates.stream().anyMatch(p -> p.test(group.getName())))
                .flatMap(group ->
                        (registerType == null ?
                                userService.findByGroupName(group.getName()) :
                                userService.findByGroupNameAndRegisterType(group.getName(), registerType)
                        ).stream())
                .filter(u -> startDate == null || u.getRegisteredDate().compareTo(startDate) >= 0)
                .filter(u -> endDate == null || u.getRegisteredDate().compareTo(endDate) <= 0)
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
