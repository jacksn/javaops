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
public class UserMail {
    private String fullName;
    private String email;
    private Date registeredDate;

    public UserMail() {
    }

    public UserMail(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public UserMail(User user) {
        this.fullName = WordUtils.capitalize(user.getFullName());
        this.email = user.getEmail();
        this.registeredDate = user.getRegisteredDate();
    }

    public String getFirstName() {
        return fullName == null ? "" : (substringBefore(capitalize(fullName), " "));
    }

    @Override
    public boolean equals(Object o) {
        return this == o || email.equals(((UserMail) o).email);
    }

    @Override
    public int hashCode() {
        return email.hashCode();
    }
}
