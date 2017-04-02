package ru.javaops.model;

import com.google.common.base.CaseFormat;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * A Group.
 */
@Entity
@Table(name = "project")
public class Project extends NamedEntity {

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private Set<Group> group = new HashSet<>();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Group> getGroup() {
        return group;
    }

    public void setGroup(Set<Group> group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return String.format("<a href='https://github.com/JavaOPs/%s' target='_blank'>%s</a>", name, CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, name));
    }
}
