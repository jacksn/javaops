package ru.javaops.to;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserToExt extends UserTo {

    @NotNull
    private Integer id;

    private String gmail;

    private String aboutMe;

    private boolean statsAgree;

    private boolean considerJobOffers;

    private boolean jobThroughTopjava;

    private boolean relocationReady;

    private String relocation;

    private boolean underRecruitment;

    private String company;

    private String resumeUrl;

    private boolean partnerResumeNotify;

    private boolean partnerCorporateStudy;

    public void setGmail(String gmail) {
        this.gmail = gmail.toLowerCase();
    }
}