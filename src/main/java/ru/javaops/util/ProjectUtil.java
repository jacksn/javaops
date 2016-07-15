package ru.javaops.util;

import ru.javaops.model.Group;
import ru.javaops.model.GroupType;
import ru.javaops.model.Project;

import java.util.Collection;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;

/**
 * gkislin
 * 13.07.2016
 */
public class ProjectUtil {

    public static ProjectUtil.ProjectProps getProjectProps(String projectName, Collection<Group> groups) {
        return new ProjectUtil.ProjectProps(
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
