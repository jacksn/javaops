package ru.javaops.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

/**
 * A Group.
 */
@Entity
@Table(name = "groups")
public class Group extends NamedEntity {

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private GroupType type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;


    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    private Set<UserGroup> groupUsers;

    public Set<UserGroup> getGroupUsers() {
        return groupUsers;
    }

    public Project getProject() {
        return project;
    }

    public GroupType getType() {
        return type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public boolean isMembers() {
        return type == GroupType.CURRENT || type == GroupType.FINISHED;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + getId() +
                ", name=" + name +
                '}';
    }
}
