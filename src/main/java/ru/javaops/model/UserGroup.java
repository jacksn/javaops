package ru.javaops.model;

import com.google.common.base.MoreObjects;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Date;

/**
 * GKislin
 * 02.09.2015.
 */
@Entity
@Table(name = "user_group", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "group_id"}, name = "user_group_unique_idx")})
public class UserGroup extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Group group;

    @Column(name = "registered_date", columnDefinition = "TIMESTAMP DEFAULT NOW()")
    private Date registeredDate = new Date();

    @Enumerated(EnumType.STRING)
    @Column(name = "register_type")
    private RegisterType registerType;

    @Enumerated(EnumType.STRING)
    @Column(name = "participation_type", nullable = true)
    private ParticipationType participationType;

    @Column(name = "channel")
    private String channel;

    @Transient
    private boolean alreadyExist = false;

    public UserGroup() {
    }

    public UserGroup(User user, Group group, RegisterType type, ParticipationType participationType, String channel) {
        this.user = user;
        this.group = group;
        this.registerType = type;
        this.channel = channel;
        this.participationType = participationType;
    }

    public String getChannel() {
        return channel;
    }

    public User getUser() {
        return user;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public RegisterType getRegisterType() {
        return registerType;
    }

    public Group getGroup() {
        return group;
    }

    public void setParticipationType(ParticipationType participationType) {
        this.participationType = participationType;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setRegisteredDate(Date registeredDate) {
        this.registeredDate = registeredDate;
    }

    public void setRegisterType(RegisterType registerType) {
        this.registerType = registerType;
    }

    public ParticipationType getParticipationType() {
        return participationType;
    }

    public boolean isAlreadyExist() {
        return alreadyExist;
    }

    public void setAlreadyExist(boolean alreadyExist) {
        this.alreadyExist = alreadyExist;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("channel", channel)
                .add("id", getId())
                .add("user", user)
                .add("group", group)
                .add("registeredDate", registeredDate)
                .add("registerType", registerType)
                .add("participationType", participationType)
                .add("alreadyExist", alreadyExist)
                .toString();
    }
}
