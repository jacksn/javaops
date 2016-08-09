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
    public UserGroup registerAtProject(UserTo userTo, ProjectProps projectProps, String channel) {
        log.info("add{} to project {}", userTo, projectProps.project);
        User user = userService.findByEmail(userTo.getEmail());
        RegisterType registerType;

        if (user != null) {
            UserUtil.updateFromTo(user, userTo);
            Set<Group> groups = findByUserId(user.getId());
            registerType = groups.stream()
                    .filter(g -> projectProps.project.equals(g.getProject()) && g.getType() == GroupType.FINISHED)
                    .findFirst().isPresent() ? RegisterType.REPEAT : RegisterType.REGISTERED;

            if (groups.stream().filter(g -> g.equals(projectProps.registeredGroup) || g.equals(projectProps.currentGroup)).findFirst().isPresent()) {
                // Already registered
                return new UserGroup(user, projectProps.registeredGroup, registerType, channel);
            }
        } else {
            user = UserUtil.createFromTo(userTo);
            registerType = RegisterType.FIRST_REGISTERED;
        }
        userService.save(user);
        Group group = (registerType == RegisterType.REPEAT) ? projectProps.currentGroup : projectProps.registeredGroup;
        return save(user, group, registerType, channel);
    }

    public UserGroup save(User user, Group group, RegisterType registerType, String channel) {
        return userGroupRepository.save(new UserGroup(user, group, registerType, channel));
    }

    public UserGroup registerAtGroup(UserTo userTo, String groupName, String channel) {
        log.info("add{} to group {}", userTo, groupName);
        Group group = findByName(groupName);
        User user = userService.findByEmail(userTo.getEmail());
        RegisterType registerType;
        if (user == null) {
            user = UserUtil.createFromTo(userTo);
            userService.save(user);
            registerType = RegisterType.FIRST_REGISTERED;
        } else {
            UserGroup ug = userGroupRepository.findByUserIdAndGroupId(user.getId(), group.getId());
            if (ug != null) {
                ug.setRegisterType(RegisterType.REPEAT);
                return ug;
            }
            registerType = RegisterType.REGISTERED;
        }
        return save(user, group, registerType, channel);
    }

    public UserGroup moveOrCreate(User u, Group sourceGroup, Group targetGroup) {
        UserGroup ug = userGroupRepository.findByUserIdAndGroupId(u.getId(), sourceGroup.getId());
        if (ug == null) {
            ug = userGroupRepository.findByUserIdAndGroupId(u.getId(), targetGroup.getId());
            if (ug == null) {
                ug = new UserGroup(u, targetGroup, RegisterType.REGISTERED, "email");
            }
        } else {
            ug.setGroup(targetGroup);
        }
        return ug;
    }

    @Transactional
    public UserGroup pay(String email, String projectName, Payment payment, ParticipationType participationType) {
        log.info("Pay from {} for {}: {}", email, projectName, payment);
        User u = userService.findExistedByEmail(email);
        ProjectProps projectProps = getProjectProps(projectName);
        UserGroup ug = moveOrCreate(u, projectProps.registeredGroup, projectProps.currentGroup);
        ug.setParticipationType(participationType);
        paymentRepository.save(payment);
        ug.setPayment(payment);
        userGroupRepository.save(ug);
        return ug;
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
