package ru.javaops.to;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class UserToExt extends UserTo {

    public UserToExt(String email, String nameSurname) {
        super(email, nameSurname);
    }

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

    private String github;

    public void setGmail(String gmail) {
        this.gmail = gmail.toLowerCase();
    }
}