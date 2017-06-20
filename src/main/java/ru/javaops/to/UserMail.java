package ru.javaops.to;

import java.util.Date;

/**
 * gkislin
 * 29.06.2016
 */
public interface UserMail {

    public String getEmail();

    public String getFullName();

    default public Date getRegisteredDate() {
        return null;
    }
}
