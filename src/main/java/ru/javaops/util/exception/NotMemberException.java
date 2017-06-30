package ru.javaops.util.exception;

/**
 * gkislin
 * 29.06.2017
 */
public class NotMemberException extends RuntimeException {
    public NotMemberException(String email) {
        super("Пользователь <b>" + email + "</b> не участник Java Online проектов");
    }

    public NotMemberException(String email, String project) {
        super("Пользователь <b>" + email + "</b> не участник проекта <b>" + project + "</b>");
    }
}
