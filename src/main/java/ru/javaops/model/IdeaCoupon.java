package ru.javaops.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * gkislin
 * 07.12.2016
 */
@Entity
@Table(name = "idea_coupon", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"coupon"}, name = "idea_coupon_unique_idx"),
        @UniqueConstraint(columnNames = {"user_id", "project_id"}, name = "idea_coupon_user_project_unique_idx")
})
public class IdeaCoupon extends BaseEntity{
    @Column(name = "coupon")
    private String coupon;

    @Column(name = "date")
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
