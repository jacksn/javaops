package ru.javaops.to;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * GKislin
 * 16.02.2016
 */
public class UserToExt extends UserTo {

    @NotNull
    private Integer id;

    @Pattern(regexp = "[_A-Za-z0-9-\\+\\.]*@gmail\\.[A-Za-z]{2,3}", message = "Неверный gmail формат")
    private String gmail;

    private String skype;

    private String aboutMe;

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }
}
