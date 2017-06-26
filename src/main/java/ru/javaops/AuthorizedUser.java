package ru.javaops;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import ru.javaops.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static java.util.Objects.requireNonNull;

/**
 * GKislin
 */
public class AuthorizedUser extends org.springframework.security.core.userdetails.User {
    private static final long serialVersionUID = 1L;

    private User user;

    public AuthorizedUser(User user) {
        super(user.getEmail(), "password", true, true, true, true, user.getRoles());
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


    public static void setAuthorized(User user, HttpServletRequest request) {
        AuthorizedUser authorizedUser = new AuthorizedUser(user);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(
                new UsernamePasswordAuthenticationToken(authorizedUser, null, authorizedUser.getAuthorities()));
        // Create a new session and add the security context.
        HttpSession session = request.getSession(true);
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
