package ru.javaops.util;

import com.google.common.base.Strings;
import ru.javaops.model.User;
import ru.javaops.to.UserTo;
import ru.javaops.to.UserToExt;

import java.util.Date;
import java.util.regex.Pattern;

import static ru.javaops.util.Util.assignNotEmpty;

/**
 * GKislin
 * 16.02.2016
 */
public class UserUtil {
    static final Pattern GMAIL_EXP = Pattern.compile("\\@gmail\\.");

    public static User createFromTo(UserTo userTo) {
        return tryFillGmail(new User(userTo.getEmail(), userTo.getNameSurname(), userTo.getLocation(), userTo.getInfoSource(), userTo.getPhone()));
    }

    public static void updateFromToExt(User user, UserToExt userToExt) {
        updateFromTo(user, userToExt);
        if (userToExt.isConsiderJobOffers() && (user.isConsiderJobOffers() == null || !user.isConsiderJobOffers()) ||
                (!Strings.isNullOrEmpty(userToExt.getResumeUrl()) && Strings.isNullOrEmpty(user.getResumeUrl()))) {
            user.setHrUpdate(new Date());
        }
        assignNotEmpty(userToExt.getAboutMe(), user::setAboutMe);
        assignNotEmpty(userToExt.getGmail(), user::setGmail);
        assignNotEmpty(userToExt.getCompany(), user::setCompany);
        assignNotEmpty(userToExt.getResumeUrl(), user::setResumeUrl);
        assignNotEmpty(userToExt.getRelocation(), user::setRelocation);
        user.setStatsAgree(userToExt.isStatsAgree());
        user.setConsiderJobOffers(userToExt.isConsiderJobOffers());
        user.setJobThroughTopjava(userToExt.isJobThroughTopjava());
        user.setRelocationReady(userToExt.isRelocationReady());
        user.setUnderRecruitment(userToExt.isUnderRecruitment());
    }

    public static void updateFromTo(User user, UserTo userTo) {
        assignNotEmpty(userTo.getNameSurname(), user::setFullName);
        assignNotEmpty(userTo.getLocation(), user::setLocation);
        assignNotEmpty(userTo.getInfoSource(), user::setInfoSource);
        assignNotEmpty(userTo.getEmail(), user::setEmail);
        assignNotEmpty(userTo.getSkype(), user::setSkype);
        user.setActive(true);
        tryFillGmail(user);
    }

    private static User tryFillGmail(User user) {
        if (GMAIL_EXP.matcher(user.getEmail()).find()) {
            user.setGmail(user.getEmail());
        }
        return user;
    }
}
