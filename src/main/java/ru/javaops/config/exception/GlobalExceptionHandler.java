package ru.javaops.config.exception;

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

/**
 * User: gkislin
 * Date: 23.09.2014
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoHandlerFoundException.class)
    @Order(Ordered.LOWEST_PRECEDENCE - 1)
    public ModelAndView noHandlerFoundHandler() throws Exception {
        return processException("Неверный запрос");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @Order(Ordered.LOWEST_PRECEDENCE - 1)
    public ModelAndView illegalArgumentHandler(HttpServletRequest req, Exception e) throws Exception {
        log.error("Illegal params in request" + req.getRequestURL(), e);
        return processException(e.getMessage() == null ? "Неверные параметры запроса" : e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @Order(Ordered.LOWEST_PRECEDENCE)
    public ModelAndView defaultHandler(HttpServletRequest req, Throwable e) throws Exception {
        log.error("Exception at request " + req.getRequestURL(), e);
        return processException(Throwables.getRootCause(e).getMessage());
    }

    private ModelAndView processException(String msg) {
        ModelAndView mav = new ModelAndView("exception");
        mav.addObject("message", msg);
        return mav;
    }
}