package ru.javaops.util;

import ru.javaops.model.User;
import ru.javaops.to.UserTo;
import ru.javaops.to.UserToExt;

import static ru.javaops.util.Util.assignNotEmpty;

/**
 * GKislin
 * 16.02.2016
 */
public class UserUtil {
    public static User createFromTo(UserTo userTo) {
        return new User(userTo.getEmail(), userTo.getNameSurname(), userTo.getLocation(), userTo.getInfoSource());
    }

    public static void updateFromToExt(User user, UserToExt userToExt) {
        updateFromTo(user, userToExt);
        assignNotEmpty(userToExt.getAboutMe(), user::setAboutMe);
        assignNotEmpty(userToExt.getSkype(), user::setSkype);
        assignNotEmpty(userToExt.getGmail(), user::setGmail);
        user.setStatsAgree(userToExt.isStatsAgree());
    }

    public static void updateFromTo(User user, UserTo userTo) {
        assignNotEmpty(userTo.getNameSurname(), user::setFullName);
        assignNotEmpty(userTo.getLocation(), user::setLocation);
        assignNotEmpty(userTo.getInfoSource(), user::setInfoSource);
        assignNotEmpty(userTo.getEmail(), user::setEmail);
        user.setActive(true);
    }
}
