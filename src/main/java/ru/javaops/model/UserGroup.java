package ru.javaops.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

/**
 * GKislin
 * 02.09.2015.
 */
@Entity
@Table(name = "user_group", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "group_id"}, name = "user_group_unique_idx")})
public class UserGroup extends BaseEntity {

    public UserGroup() {
    }

    public UserGroup(User user, Group group, RegisterType type, String channel) {
        this.user = user;
        this.group = group;
        this.registerType = type;
        this.channel = channel;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Group group;

    @Enumerated(EnumType.STRING)
    @Column(name = "register_type")
    private RegisterType registerType;

    @Enumerated(EnumType.STRING)
    @Column(name = "participation_type", nullable = true)
    private ParticipationType participationType;

    @Column(name = "channel")
    private String channel;

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Payment payment;

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

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public void setParticipationType(ParticipationType participationType) {
        this.participationType = participationType;
    }
}
