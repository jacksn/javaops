package ru.javaops.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.javaops.model.Currency;
import ru.javaops.model.ParticipationType;
import ru.javaops.model.Payment;
import ru.javaops.model.UserGroup;
import ru.javaops.service.GroupService;
import ru.javaops.service.MailService;
import ru.javaops.service.UserService;
import ru.javaops.to.UserTo;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * GKislin
 */

@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @Autowired
    private GroupService groupService;

    @RequestMapping(method = DELETE)
    public void delete(@RequestParam("email") String email) {
        userService.deleteByEmail(email);
    }

    @RequestMapping(value = "/pay", method = POST)
    public String pay(@RequestParam("project") String project, @Valid UserTo userTo,
                      @RequestParam("sum") int sum, @RequestParam("currency") Currency currency, @RequestParam("comment") String comment,
                      @RequestParam(value = "type", required = false) ParticipationType participationType,
                      @RequestParam(value = "channel", required = false) String channel,
                      @RequestParam(value = "template", required = false) String template) {
        UserGroup ug = groupService.pay(userTo, project, new Payment(sum, currency, comment), participationType, channel);
        return (template == null) ? "Paid" : mailService.sendToUser(template, ug.getUser());
    }
}