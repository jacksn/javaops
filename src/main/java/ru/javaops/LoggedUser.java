package ru.javaops;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.javaops.model.User;

import static java.util.Objects.requireNonNull;

/**
 * GKislin
 */
public class LoggedUser extends org.springframework.security.core.userdetails.User {
    private static final long serialVersionUID = 1L;

    private int id;

    public LoggedUser(User user) {
        super(user.getEmail(), user.getPassword(), user.isActive(), true, true, true, user.getRoles());
        this.id = user.getId();
    }

    public static LoggedUser safeGet() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        Object user = auth.getPrincipal();
        return (user instanceof LoggedUser) ? (LoggedUser) user : null;
    }

    public static LoggedUser get() {
        LoggedUser user = safeGet();
        requireNonNull(user, "No authorized user found");
        return user;
    }

    public static int id() {
        return get().id;
    }

    @Override
    public String toString() {
        return "User (" + id + ',' + getUsername() + ")";
    }
}
