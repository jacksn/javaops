package ru.javaops.to;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * GKislin
 * 16.02.2016
 */
@Getter
@Setter
@ToString
public class UserTo {
    @NotEmpty(message = "Поле email не может быть пустым")
    @Email
    private String email;

    private String nameSurname;
    private String location;
    private String infoSource;
    private String phone;
    private String skype;

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }

    public void setLocation(String location) {
        this.location = location == null ? null : location.trim().toLowerCase();
    }
}
