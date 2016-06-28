package ru.javaops.to;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

/**
 * gkislin
 * 29.06.2016
 */
public class UserStat {
    private final String fullName;
    private final String email;
    private final String location;
    private final String aboutMe;
    private final String skype;

    public UserStat(String fullName, String email, String location, String aboutMe, String skype) {
        this.fullName = WordUtils.capitalize(fullName);
        this.email = email;
        this.location = WordUtils.capitalize(location, ' ', '-', '.');
        this.aboutMe = StringUtils.replace(aboutMe, "\n", "<br/>");
        this.skype = skype;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getLocation() {
        return location;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public String getSkype() {
        return skype;
    }
}
