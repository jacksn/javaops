package ru.javaops.config.exception;

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.javaops.util.exception.ErrorInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * Date: 20.02.2016
 */
@ControllerAdvice(annotations = RestController.class)
public class ExceptionInfoHandler {
    private static final Logger log = LoggerFactory.getLogger(ExceptionInfoHandler.class);

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ErrorInfo handleError(HttpServletRequest req, Exception e) {
        log.error("Exception at request " + req.getRequestURI(), e);
        return new ErrorInfo(req.getRequestURL(), Throwables.getRootCause(e));
    }
}
