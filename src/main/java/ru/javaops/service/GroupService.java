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
import ru.javaops.util.UserUtil;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

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
        ParticipationType participationType;

        if (user != null) {
            UserUtil.updateFromTo(user, userTo);
            Set<Group> groups = findByUserId(user.getId());
            participationType = groups.stream()
                    .filter(g -> projectProps.project.equals(g.getProject()) && g.getType() == GroupType.FINISHED)
                    .findFirst().isPresent() ? ParticipationType.REPEAT : ParticipationType.REGISTERED;

            if (groups.stream().filter(g -> g.equals(projectProps.registeredGroup) || g.equals(projectProps.currentGroup)).findFirst().isPresent()) {
                // Already registered
                return new UserGroup(user, projectProps.registeredGroup, participationType, channel);
            }
        } else {
            user = UserUtil.createFromTo(userTo);
            participationType = ParticipationType.FIRST_REGISTERED;
        }
        userService.save(user);
        Group group = (participationType == ParticipationType.REPEAT) ? projectProps.currentGroup : projectProps.registeredGroup;
        UserGroup userGroup = new UserGroup(user, group, participationType, channel);
        return userGroupRepository.save(userGroup);
    }

    public UserGroup moveOrCreate(User u, Group sourceGroup, Group targetGroup) {
        UserGroup ug = userGroupRepository.findByUserIdAndGroupId(u.getId(), sourceGroup.getId());
        if (ug == null) {
            ug = new UserGroup(u, targetGroup, ParticipationType.REGISTERED, "email");
        } else {
            ug.setGroup(targetGroup);
        }
        return ug;
    }

    @Transactional
    public UserGroup pay(String email, String projectName, Payment payment) {
        log.info("Pay from {} for {}: {}", email, payment);
        User u = userService.findExistedByEmail(email);
        ProjectProps projectProps = getProjectProps(projectName);
        UserGroup ug = moveOrCreate(u, projectProps.registeredGroup, projectProps.currentGroup);
        paymentRepository.save(payment);
        ug.setPayment(payment);
        userGroupRepository.save(ug);
        return ug;
    }

    public ProjectProps getProjectProps(String projectName) {
        Collection<Group> groups = getAll();
        return new ProjectProps(
                getGroupByProjectAndType(groups, projectName, GroupType.REGISTERED),
                getGroupByProjectAndType(groups, projectName, GroupType.CURRENT));
    }

    private static Group getGroupByProjectAndType(Collection<Group> groups, String projectName, GroupType type) {
        Optional<Group> group = groups.stream()
                .filter(g -> g.getProject() != null && g.getProject().getName().equals(projectName) && (g.getType() == type))
                .findFirst();
        checkState(group.isPresent(), "В проекте %s отсутствуют группы c типом %s", projectName, type);
        return group.get();
    }

    public Set<User> filterUserByGroupNames(String includes, String excludes) {
        final List<Group> groups = getAll();
        Set<User> includeUsers = filterUserByGroupNames(groups, includes);
        if (StringUtils.isNoneEmpty(excludes)) {
            Set<User> excludeUsers = filterUserByGroupNames(groups, excludes);
            includeUsers.removeAll(excludeUsers);
        }
        return includeUsers;
    }

    private Set<User> filterUserByGroupNames(List<Group> groups, String groupNames) {
        List<Predicate<String>> predicates = getMatcher(groupNames);

        // filter users by predicates
        return groups.stream().filter(group -> predicates.stream().anyMatch(p -> p.test(group.getName())))
                .flatMap(group -> userService.findByGroupName(group.getName()).stream())
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

    public static class ProjectProps {
        public final Group registeredGroup;
        public final Group currentGroup;
        public final Project project;

        public ProjectProps(Group registeredGroup, Group currentGroup) {
            this.registeredGroup = registeredGroup;
            this.currentGroup = currentGroup;
            this.project = registeredGroup.getProject();
        }
    }
}
