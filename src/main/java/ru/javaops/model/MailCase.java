package ru.javaops.model;

import ru.javaops.to.UserMail;
import ru.javaops.to.UserMailImpl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
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

    @Column(name = "email", nullable = false)
    @NotNull
    private String email;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "template", nullable = false)
    @NotNull
    private String template;

    @Column(name = "result", nullable = false)
    @NotNull
    private String result;

    protected MailCase() {
    }

    public MailCase(UserMail userMail, String template, String result) {
        this.fullName = userMail.getFullName();
        this.email = userMail.getEmail();
        this.template = template;
        this.result = result;
        this.datetime = new Date();
    }

    public UserMail getUserMail() {
        return new UserMailImpl(fullName, email);
    }

    public String getTemplate() {
        return template;
    }

    public String getResult() {
        return result;
    }
}
