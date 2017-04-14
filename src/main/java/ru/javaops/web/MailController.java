package ru.javaops.web;

import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.javaops.service.GroupService;
import ru.javaops.service.MailService;
import ru.javaops.service.MailService.GroupResult;
import ru.javaops.service.SqlService;
import ru.javaops.to.UserMail;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * GKislin
 */

@RestController
@RequestMapping(value = "/api/mail", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class MailController {

    @Autowired
    private MailService mailService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private SqlService sqlService;

    @RequestMapping(value = "/test", method = POST)
    public ResponseEntity<String> sendTest(@RequestParam("template") String template) {
        String result = mailService.sendTest(template);
        return new ResponseEntity<>(result, MailService.isOk(result) ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/to-user", method = POST)
    public ResponseEntity<String> sendToUser(@RequestParam("template") String template, @RequestParam("email") String email) {
        String result = mailService.sendToUser(template, email);
        return new ResponseEntity<>(result, MailService.isOk(result) ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/by-sql", method = POST)
    public ResponseEntity<GroupResult> sendToUsersByLocation(@RequestParam("template") String template, @RequestParam("sql_key") String sqlKey) {
        Set<UserMail> users = sqlService.getUsers(sqlKey);
        return sendToGroup(template, users);
    }

    @RequestMapping(value = "/to-users", method = POST)
    public ResponseEntity<GroupResult> sendToUsers(@RequestParam("template") String template, @RequestParam("emails") String emails) {
        GroupResult groupResult = mailService.sendToEmailList(template, Splitter.on(',').trimResults().omitEmptyStrings().splitToList(emails));
        return getGroupResultResponseEntity(groupResult);
    }

    @RequestMapping(value = "/to-groups", method = POST)
    public ResponseEntity<GroupResult> sendToGroup(@RequestParam("template") String template, @RequestParam("includes") String includes,
                                                   @RequestParam(value = "excludes", required = false) String excludes,
                                                   @RequestParam(value = "startRegisteredDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startRegisteredDate,
                                                   @RequestParam(value = "endRegisteredDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endRegisteredDate) {
        return sendToGroup(template, groupService.filterUserByGroupNames(includes, excludes, startRegisteredDate, endRegisteredDate));
    }

    @RequestMapping(value = "/resend", method = POST)
    public ResponseEntity<GroupResult> resend(@RequestParam("template") String template) {
        GroupResult groupResult = mailService.resendTodayFailed(template);
        return getGroupResultResponseEntity(groupResult);
    }

    private ResponseEntity<GroupResult> sendToGroup(String template, Set<UserMail> users) {
        if (users.isEmpty()) {
            return getGroupResultResponseEntity(new GroupResult(0, Collections.emptyList(), null));
        }
        GroupResult groupResult = mailService.sendToUserList(template, users);
        return getGroupResultResponseEntity(groupResult);
    }

    private ResponseEntity<GroupResult> getGroupResultResponseEntity(GroupResult groupResult) {
        log.info(groupResult.toString());
        return new ResponseEntity<>(groupResult, groupResult.isOk() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
