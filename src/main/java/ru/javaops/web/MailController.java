package ru.javaops.web;

import com.google.common.base.Splitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.javaops.model.RegisterType;
import ru.javaops.model.User;
import ru.javaops.service.GroupService;
import ru.javaops.service.MailService;

import java.util.Set;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * GKislin
 */

@RestController
@RequestMapping(value = "/api/mail", produces = MediaType.APPLICATION_JSON_VALUE)
public class MailController {

    @Autowired
    private MailService mailService;

    @Autowired
    private GroupService groupService;

    @RequestMapping(value = "/test", method = POST)
    public ResponseEntity<String> sendToUser(@Param("template") String template) {
        String result = mailService.sendTest(template);
        return new ResponseEntity<>(result, MailService.isOk(result) ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/to-user", method = POST)
    public ResponseEntity<String> sendToUser(@Param("template") String template, @Param("email") String email) {
        String result = mailService.sendToUser(template, email);
        return new ResponseEntity<>(result, MailService.isOk(result) ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/to-users", method = POST)
    public ResponseEntity<MailService.GroupResult> sendToProjectByGroupType(@Param("template") String template, @Param("emails") String emails) {
        MailService.GroupResult groupResult = mailService.sendToEmailList(template, Splitter.on(',').trimResults().omitEmptyStrings().splitToList(emails));
        return new ResponseEntity<>(groupResult, groupResult.isOk() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/to-groups", method = POST)
    public ResponseEntity<MailService.GroupResult> sendToGroup(@Param("template") String template, @Param("includes") String includes,
                                                               @RequestParam(value = "excludes", required = false) String excludes,
                                                               @RequestParam(value = "reg-type", required = false) RegisterType registerType) {

        Set<User> users = groupService.filterUserByGroupNames(includes, excludes, registerType);
        MailService.GroupResult groupResult = mailService.sendToUserList(template, users);
        return new ResponseEntity<>(groupResult, groupResult.isOk() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
