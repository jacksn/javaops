package ru.javaops.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * gkislin
 * 14.05.2017
 */
@UtilityClass
@Slf4j
public class WebUtil {
    public static void logWarn(HttpServletRequest req) {
        log.warn("+++ Suspicious request: '" + req.getRequestURI()
                + ", params=[" + req.getParameterMap().entrySet().stream().map(e -> e.getKey() + ':' + Arrays.toString(e.getValue())).collect(Collectors.joining(","))
                + "], IP=" + req.getRemoteAddr()
                + ", FORWARDED=" + req.getHeader("X-FORWARDED-FOR"));
    }
}
