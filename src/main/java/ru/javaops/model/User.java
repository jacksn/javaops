package ru.javaops.model;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;
import ru.javaops.to.UserMail;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Date;
import java.util.EnumSet;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.substringBefore;

/**
 * User: gkislin
 */
@Entity
@Table(name = "users")
public class User extends BaseEntity implements UserMail {
    private static final long PARTNER_RESUME_NOTIFY = 0x1;
    private static final long PARTNER_CORPORATE_STUDY = 0x2;
    private static final long PARTNER_DIRECT_EMAIL = 0x4;

    @Column(name = "email", nullable = false, unique = true)
    @Email
    @NotEmpty
    private String email;

    @Column(name = "full_name", length = 50)
    private String fullName;

    @Column(name = "password")
    @Length(min = 5)
    private String password;

    @Email
    @Size(max = 100)
    @Column(length = 100, unique = true)
    private String gmail;

    @Size(max = 50)
    private String location;

    @Size(max = 50)
    private String phone;

    @Size(max = 100)
    @Column(name = "info_source", length = 100)
    private String infoSource;

    @Column(name = "about_me")
    private String aboutMe;

    @Column(name = "stats_agree")
    private boolean statsAgree;

    @Column(name = "consider_job_offers")
    private Boolean considerJobOffers;

    @Column(name = "relocation_ready")
    private Boolean relocationReady;

    @Column(name = "job_through_topjava")
    private Boolean jobThroughTopjava;

    @Column(name = "under_recruitment")
    private Boolean underRecruitment;

    @URL
    @Column(name = "resume_url")
    private String resumeUrl;

    @URL
    @Size
    @Column
    private String website;

    @Column
    private String company;

    @Size(max = 50)
    @Column
    private String skype;

    @Size(max = 100)
    @Column
    private String github;

    @Size(max = 100)
    @Column
    private String vk;

    @Column(name = "active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean active = true;

    @Column(name = "registered_date", columnDefinition = "TIMESTAMP DEFAULT NOW()")
    private Date registeredDate = new Date();

    @Column
    private String relocation;

    @Column
    private String mark;

    @Column(name = "bonus", columnDefinition = "INT DEFAULT 0")
    private int bonus = 0;

    @Column(name = "activated_date")
    private Date activatedDate;

    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @ElementCollection(fetch = FetchType.LAZY)
    private Set<Role> roles;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<UserGroup> userGroups;

    private LocalDate hrUpdate;

    private String comment;

    @Column(name = "partner_flag", columnDefinition = "bigint default 0")
    private long partnerFlag;

    public User() {
    }

    public User(String email, String nameSurname, String location, String infoSource, String skype) {
        this(null, email, nameSurname, location, infoSource, skype);
    }

    public User(Integer id, String email, String fullName, String location, String infoSource, String skype) {
        super(id);
        this.email = email;
        this.fullName = fullName;
        this.location = location;
        this.infoSource = infoSource;
        this.skype = skype;
    }

    @Override
    public String getEmail() {
        return email;
    }

    // Exception evaluating SpringEL expression: "user.firstName" for default method in UserMail
    public String getFirstName() {
        return fullName == null ? "" : (substringBefore(capitalize(fullName), " "));
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setActive(boolean activated) {
        this.active = activated;
    }

    public void setActivatedDate(Date activatedDate) {
        this.activatedDate = activatedDate;
    }

    public boolean isActive() {
        return active;
    }

    public Set<Role> getRoles() {
        if (roles == null) {
            roles = EnumSet.noneOf(Role.class);
        }
        return roles;
    }

    public String getPassword() {
        return password;
    }

    public Set<UserGroup> getUserGroups() {
        return userGroups;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setInfoSource(String infoSource) {
        this.infoSource = infoSource;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLocation() {
        return location;
    }

    public String getInfoSource() {
        return infoSource;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public String getGmail() {
        return gmail;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public String getSkype() {
        return skype;
    }

    public String getWebsite() {
        return website;
    }

    public String getCompany() {
        return company;
    }

    public Boolean isJobThroughTopjava() {
        return jobThroughTopjava;
    }

    public Boolean isUnderRecruitment() {
        return underRecruitment;
    }

    public void setUnderRecruitment(Boolean underRecruitment) {
        this.underRecruitment = underRecruitment;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public String getGithub() {
        return github;
    }

    public String getVk() {
        return vk;
    }

    @Override
    public Date getRegisteredDate() {
        return registeredDate;
    }

    public Date getActivatedDate() {
        return activatedDate;
    }

    public Boolean isConsiderJobOffers() {
        return considerJobOffers;
    }

    public void setConsiderJobOffers(Boolean considerJobOffers) {
        this.considerJobOffers = considerJobOffers;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setJobThroughTopjava(Boolean jobThroughTopjava) {
        this.jobThroughTopjava = jobThroughTopjava;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    public boolean isStatsAgree() {
        return statsAgree;
    }

    public void setStatsAgree(boolean statsAgree) {
        this.statsAgree = statsAgree;
    }

    public String getPhone() {
        return phone;
    }

    public Boolean isRelocationReady() {
        return relocationReady;
    }

    public void setRelocationReady(Boolean relocationReady) {
        this.relocationReady = relocationReady;
    }

    public LocalDate getHrUpdate() {
        return hrUpdate;
    }

    public void setHrUpdate(LocalDate hrUpdate) {
        this.hrUpdate = hrUpdate;
    }

    public String getRelocation() {
        return relocation;
    }

    public void setRelocation(String relocation) {
        this.relocation = relocation;
    }

    public String getComment() {
        return comment;
    }

    public int getBonus() {
        return bonus;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isPartner() {
        return hasRole(Role.ROLE_PARTNER);
    }

    public boolean isAdmin() {
        return hasRole(Role.ROLE_ADMIN);
    }

    public boolean isPartnerResumeNotify() {
        return hasPartnerFlag(PARTNER_RESUME_NOTIFY);
    }

    public boolean isPartnerCorporateStudy() {
        return hasPartnerFlag(PARTNER_CORPORATE_STUDY);
    }

    public boolean isPartnerDirectEmail() {
        return hasPartnerFlag(PARTNER_DIRECT_EMAIL);
    }

    public void setPartnerResumeNotify(boolean flag) {
        setPartnerFlag(PARTNER_RESUME_NOTIFY, flag);
    }

    public void setPartnerCorporateStudy(boolean flag) {
        setPartnerFlag(PARTNER_CORPORATE_STUDY, flag);
    }

    public void setPartnerDirectEmail(boolean flag) {
        setPartnerFlag(PARTNER_DIRECT_EMAIL, flag);
    }

    public boolean isMember() {
        return hasRole(Role.ROLE_MEMBER);
    }

    private boolean hasRole(Role role) {
        return roles != null && roles.contains(role);
    }

    private boolean hasPartnerFlag(long mask) {
        return (partnerFlag & mask) != 0;
    }

    public int addBonus(int bonus) {
        this.bonus += bonus;
        return bonus;
    }

    private void setPartnerFlag(long mask, boolean flag) {
        if (flag) {
            partnerFlag |= mask;
        } else {
            partnerFlag &= ~mask;
        }
    }

    public String getMark() {
        return mark;
    }

    @Override
    public String toString() {
        return "User (" +
                "id=" + getId() +
                ", email=" + email +
                ", fullName='" + fullName + '\'' +
                ", location=" + location +
                ", infoSource=" + infoSource +
                ')';
    }

    @Override
    public boolean equals(Object o) {
        return this == o || email.equals(((UserMail) o).getEmail());
    }

    @Override
    public int hashCode() {
        return email.hashCode();
    }
}
