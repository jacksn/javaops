package ru.javaops.to;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class UserToExt extends UserTo {

    @NotNull
    private Integer id;

    @Pattern(regexp = "[_A-Za-z0-9-\\+\\.]*@gmail\\.[A-Za-z]{2,3}", message = "Неверный gmail формат")
    @NotEmpty(message = "для авторизации требуется gmail")
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