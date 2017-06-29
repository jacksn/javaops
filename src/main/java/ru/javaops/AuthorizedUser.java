package ru.javaops;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import ru.javaops.model.User;
import ru.javaops.to.UserToExt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static java.util.Objects.requireNonNull;

/**
 * GKislin
 */
@Slf4j
public class AuthorizedUser extends org.springframework.security.core.userdetails.User {
    private static final long serialVersionUID = 1L;
    public static final String PRE_AUTHORIZED = "PRE_AUTHORIZED";

    private User user;

    public AuthorizedUser(User user) {
        super(user.getEmail(), user.getPassword() != null ? user.getPassword() : "dummy", true, true, true, true, user.getRoles());
        this.user = user;
    }

    public static AuthorizedUser safeGet() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        Object user = auth.getPrincipal();
        return (user instanceof AuthorizedUser) ? (AuthorizedUser) user : null;
    }

    public static AuthorizedUser get() {
        AuthorizedUser user = safeGet();
        requireNonNull(user, "Требуется авторизация");
        return user;
    }

    public static void setPreAuthorized(UserToExt userToExt, HttpServletRequest request) {
        log.info("setPreAuthorized for '{}', '{}'", userToExt.getEmail(), userToExt.getNameSurname());
        HttpSession session = request.getSession(true);
        session.setAttribute(PRE_AUTHORIZED, userToExt);
    }

    public static UserToExt getPreAuthorized(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (UserToExt) session.getAttribute(PRE_AUTHORIZED);
        }
        return null;
    }

    public static void setAuthorized(User user, HttpServletRequest request) {
        log.info("setAuthorized for '{}', '{}'", user.getEmail(), user.getFullName());
        AuthorizedUser authorizedUser = new AuthorizedUser(user);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(
                new UsernamePasswordAuthenticationToken(authorizedUser, null, authorizedUser.getAuthorities()));
        // Create a new session and add the security context.
        HttpSession session = request.getSession(true);
        session.removeAttribute(PRE_AUTHORIZED);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
    }

    public static User user() {
        AuthorizedUser authorizedUser = safeGet();
        return authorizedUser == null ? null : authorizedUser.user;
    }

    @Override
    public String toString() {
        return user == null ? "noAuth" : user.toString();
    }

    public static void updateUser(User user) {
        get().user = user;
    }
}
