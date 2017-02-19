package ru.javaops.model;

import org.springframework.security.core.GrantedAuthority;

/**
 * User: gkislin
 * Date: 22.08.2014
 */
public enum Role implements GrantedAuthority {
    ROLE_MEMBER,
    ROLE_ASSISTANT,
    ROLE_PARTNER,
    ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}