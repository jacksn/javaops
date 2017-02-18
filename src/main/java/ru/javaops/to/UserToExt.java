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

    private boolean statsAgree;

    private boolean considerJobOffers;

    private boolean jobThruTopjava;

    private String company;

    private String resumeUrl;

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail.toLowerCase();
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

    public boolean isStatsAgree() {
        return statsAgree;
    }

    public void setStatsAgree(boolean statsAgree) {
        this.statsAgree = statsAgree;
    }

    public boolean isConsiderJobOffers() {
        return considerJobOffers;
    }

    public void setConsiderJobOffers(boolean considerJobOffers) {
        this.considerJobOffers = considerJobOffers;
    }

    public boolean isJobThruTopjava() {
        return jobThruTopjava;
    }

    public void setJobThruTopjava(boolean jobThruTopjava) {
        this.jobThruTopjava = jobThruTopjava;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }
}