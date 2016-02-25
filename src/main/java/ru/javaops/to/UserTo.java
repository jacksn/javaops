package ru.javaops.to;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * GKislin
 * 16.02.2016
 */
public class UserTo {
    @NotEmpty(message = "Поле email не может быть пустым")
    private String email;

    private String nameSurname;
    private String location;
    private String infoSource;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }

    public String getNameSurname() {
        return nameSurname;
    }

    public void setNameSurname(String nameSurname) {
        this.nameSurname = nameSurname;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getInfoSource() {
        return infoSource;
    }

    public void setInfoSource(String infoSource) {
        this.infoSource = infoSource;
    }

    @Override
    public String toString() {
        return "UserTo (" +
                "email='" + email + '\'' +
                ", nameSurname='" + nameSurname + '\'' +
                ", location='" + location + '\'' +
                ", infoSource='" + infoSource + '\'' +
                ')';
    }
}
