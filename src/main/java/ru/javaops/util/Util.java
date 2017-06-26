package ru.javaops.util;

import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GKislin
 * 15.02.2016
 */
public class Util {
    private static Pattern MAIL_TITLE = Pattern.compile("<title>(.+)</title>", Pattern.MULTILINE);

    public static boolean assignNotEmpty(String value, Consumer<String> setter) {
        if (StringUtils.hasText(value)) {
            setter.accept(value);
            return true;
        }
        return false;
    }

    public static boolean assignNotOverride(String value, String oldValue, Consumer<String> setter) {
        if (!StringUtils.hasText(oldValue)) {
            setter.accept(value);
            return true;
        }
        return false;
    }

    public static void assign(String value, Consumer<String> setter) {
        if (StringUtils.hasText(value)) {
            setter.accept(value);
        } else {
            setter.accept(null);
        }
    }

    public static String toString(Resource resource) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            return FileCopyUtils.copyToString(br);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String getErrorMessage(BindingResult result) {
        StringBuilder sb = new StringBuilder();
        result.getFieldErrors().forEach(fe -> sb.append(fe.getDefaultMessage()).append("<br/>"));
        return sb.toString();
    }

    public static String getTitle(String template) {
        Matcher m = MAIL_TITLE.matcher(template);
        return m.find() ? m.group(1) : null;
    }
}
