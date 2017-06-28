package ru.javaops.util;

import com.google.common.base.Strings;
import org.springframework.util.StringUtils;
import ru.javaops.model.User;
import ru.javaops.to.UserTo;
import ru.javaops.to.UserToExt;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

import static ru.javaops.util.Util.*;

/**
 * GKislin
 * 16.02.2016
 */
public class UserUtil {
    static final Pattern GMAIL_EXP = Pattern.compile("\\@gmail\\.");

    public static User createFromTo(UserTo userTo) {
        User user = new User(userTo.getEmail(), userTo.getNameSurname(), userTo.getLocation(), userTo.getInfoSource(), userTo.getSkype());
        tryFillGmail(user);
        return user;
    }

    public static User createFromToExt(UserToExt userToExt) {
        User user = createFromTo(userToExt);
        user.setGithub(userToExt.getGithub());
        return user;
    }

    public static void updateFromToExt(User user, UserToExt userToExt) {
        updateFromTo(user, userToExt);
        if (user.isMember()) {
            if (!StringUtils.hasText(userToExt.getGmail())) {
                throw new IllegalArgumentException("Заполните gmail, он требуется для авторизации");
            } else if (!GMAIL_EXP.matcher(userToExt.getGmail()).find()) {
                throw new IllegalArgumentException("Неверный формат gmail");
            }
            user.setStatsAgree(userToExt.isStatsAgree());
            user.setJobThroughTopjava(userToExt.isJobThroughTopjava());
        }
        if ((user.getHrUpdate() == null || user.getHrUpdate().isBefore(LocalDate.now())) // not switched back
                && userToExt.isConsiderJobOffers() && !Strings.isNullOrEmpty(userToExt.getResumeUrl())  // visible for HR
                && (user.isConsiderJobOffers() == null || !user.isConsiderJobOffers() || Strings.isNullOrEmpty(user.getResumeUrl()))) {  // was not visible for HR
            user.setHrUpdate(LocalDate.now());

        } else if (!userToExt.isConsiderJobOffers() && user.isConsiderJobOffers() != null && user.isConsiderJobOffers()) {  // stop job considering
            user.setHrUpdate(LocalDate.now().plus(90, ChronoUnit.DAYS));
        }
        if (user.isPartner()) {
            user.setPartnerResumeNotify(userToExt.isPartnerResumeNotify());
            user.setPartnerCorporateStudy(userToExt.isPartnerCorporateStudy());
        }
        user.setConsiderJobOffers(userToExt.isConsiderJobOffers());
        user.setUnderRecruitment(userToExt.isUnderRecruitment());

        assign(userToExt.getAboutMe(), user::setAboutMe);
        assign(userToExt.getGmail(), user::setGmail);
        assign(userToExt.getCompany(), user::setCompany);
        assign(userToExt.getResumeUrl(), user::setResumeUrl);
        assign(userToExt.getRelocation(), user::setRelocation);
    }

    public static void updateFromTo(User user, UserTo userTo) {
        assignNotEmpty(userTo.getNameSurname(), user::setFullName);
        assignNotEmpty(userTo.getInfoSource(), user::setInfoSource);
        assign(userTo.getLocation(), user::setLocation);
        assign(userTo.getSkype(), user::setSkype);
        user.setActive(true);
        tryFillGmail(user);
    }

    public static boolean updateFromAuth(User user, UserToExt userToExt) {
        boolean assign = assignNotOverride(userToExt.getNameSurname(), user.getFullName(), user::setFullName);
        assign |= assignNotEmpty(userToExt.getGithub(), user::setGithub);
        assign |= tryFillGmail(user);
        return assign;
    }

    private static boolean tryFillGmail(User user) {
        if (user.getGmail() == null && GMAIL_EXP.matcher(user.getEmail()).find()) {
            user.setGmail(user.getEmail());
            return true;
        }
        return false;
    }

    public static String normalize(String aboutMe) {
        return aboutMe == null ? "" : aboutMe.replace("\r\n", "<br/>")
                .replace("\n", "<br/>")
                .replace("\r", "<br/>");
    }
}
