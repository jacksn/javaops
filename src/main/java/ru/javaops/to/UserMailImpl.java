package ru.javaops.to;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.text.WordUtils;
import ru.javaops.model.User;

import java.util.Date;

import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.substringBefore;

/**
 * gkislin
 * 29.06.2016
 */
@Getter
@Setter
public class UserMailImpl implements UserMail {
    private String fullName;
    private String email;
    private Date registeredDate;
    private int bonus;

    public UserMailImpl() {
    }

    public UserMailImpl(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public UserMailImpl(User user) {
        this.fullName = WordUtils.capitalize(user.getFullName());
        this.email = user.getEmail();
        this.registeredDate = user.getRegisteredDate();
        this.bonus = user.getBonus();
    }

    // Exception evaluating SpringEL expression: "user.firstName" for default method in UserMail
    public String getFirstName() {
        return getFullName() == null ? "" : (substringBefore(capitalize(getFullName()), " "));
    }

    @Override
    public boolean equals(Object o) {
        return this == o || email.equals(((UserMail) o).getEmail());
    }

    @Override
    public int hashCode() {
        return email.hashCode();
    }
}
