package ru.javaops.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * GKislin
 * 10.01.2016
 */
@Entity
@Table(name = "mail_case")
public class MailCase extends BaseEntity {
    @Column(name = "datetime", columnDefinition = "TIMESTAMP DEFAULT NOW()", nullable = false)
    @NotNull
    private Date datetime;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(name = "template", nullable = false)
    @NotNull
    private String template;

    @Column(name = "result", nullable = false)
    @NotNull
    private String result;

    protected MailCase() {
    }

    public MailCase(User user, String template, String result) {
        this.user = user;
        this.template = template;
        this.result = result;
        this.datetime = new Date();
    }

    public Date getDatetime() {
        return datetime;
    }

    public User getUser() {
        return user;
    }

    public String getTemplate() {
        return template;
    }

    public String getResult() {
        return result;
    }
}
