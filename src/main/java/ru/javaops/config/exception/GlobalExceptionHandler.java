package ru.javaops.config.exception;

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import ru.javaops.AuthorizedUser;

import javax.servlet.http.HttpServletRequest;

/**
 * User: gkislin
 * Date: 23.09.2014
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView noHandlerFoundHandler() throws Exception {
        return processException("Неверный запрос");
    }

    @ExceptionHandler(NoPartnerException.class)
    public ModelAndView noPartnerException(HttpServletRequest req, NoPartnerException pe) throws Exception {
        log.error("Illegal partner email in request " + req.getRequestURL());
        return new ModelAndView("noRegisteredPartner", "email", pe.getPartnerKey());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView illegalArgumentHandler(HttpServletRequest req, Exception e) throws Exception {
        log.error("Illegal params in request " + req.getRequestURL(), e);
        return processException(e.getMessage() == null ? "Неверные параметры запроса" : e.getMessage());
    }

/*
    @ExceptionHandler(ValidationException.class)
    public ModelAndView bindValidationError(HttpServletRequest req, BindingResult result) {
        log.error("Illegal binding in request " + req.getRequestURL());
        StringBuilder sb = new StringBuilder();
        result.getFieldErrors().forEach(fe -> sb.append(Strings.isNullOrEmpty(fe.getField()) ? "Поле формы" : fe.getField()).append(" ").append(fe.getDefaultMessage()).append("<br>"));
        return processException(sb.toString());
    }
*/

    @ExceptionHandler(Throwable.class)
    public ModelAndView defaultHandler(HttpServletRequest req, Throwable e) throws Exception {
        log.error("Exception at request " + req.getRequestURL(), e);
        return processException(Throwables.getRootCause(e).getMessage());
    }

    private ModelAndView processException(String msg) {
        ModelAndView modelAndView = new ModelAndView("exception", "message", msg);
        modelAndView.getModelMap().addAttribute("authUser", AuthorizedUser.user());
        return modelAndView;
    }
}