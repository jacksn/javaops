package ru.javaops.util;

import ru.javaops.model.Group;
import ru.javaops.model.GroupType;
import ru.javaops.model.Project;

import java.util.Collection;
import java.util.Optional;

/**
 * gkislin
 * 13.07.2016
 */
public class ProjectUtil {

    public static ProjectUtil.ProjectProps getProjectProps(String projectName, Collection<Group> groups) {
        return new ProjectUtil.ProjectProps(
                getExistedGroupByProjectAndType(groups, projectName, GroupType.REGISTERED),
                getExistedGroupByProjectAndType(groups, projectName, GroupType.CURRENT));
    }

    public static Optional<Group> getGroupByProjectAndType(Collection<Group> groups, String projectName, GroupType type) {
        return groups.stream()
                .filter(g -> g.getProject() != null && g.getProject().getName().equals(projectName) && (g.getType() == type))
                .findFirst();
    }

    public static Group getExistedGroupByProjectAndType(Collection<Group> groups, String projectName, GroupType type) {
        return getGroupByProjectAndType(groups, projectName, type)
                .orElseThrow(() -> new IllegalStateException("В проекте " + projectName + " отсутствуют группы c типом " + type));
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
