package ru.javaops.to;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.SafeHtml;
import org.hibernate.validator.constraints.URL;

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

    @SafeHtml
    private String gmail;

    @SafeHtml
    private String aboutMe;

    private boolean statsAgree;

    private boolean considerJobOffers;

    private boolean jobThroughTopjava;

    private boolean relocationReady;

    @SafeHtml
    private String relocation;

    private boolean underRecruitment;

    @SafeHtml
    private String company;

    @URL
    private String resumeUrl;

    private boolean partnerResumeNotify;

    private boolean partnerCorporateStudy;

    private String github;

    public void setGmail(String gmail) {
        this.gmail = gmail.toLowerCase();
    }
}