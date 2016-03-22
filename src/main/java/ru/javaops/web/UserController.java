package ru.javaops.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.javaops.model.Currency;
import ru.javaops.model.ParticipationType;
import ru.javaops.model.Payment;
import ru.javaops.model.User;
import ru.javaops.service.GroupService;
import ru.javaops.service.MailService;
import ru.javaops.service.UserService;

import java.util.Collection;

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
    public void delete(@Param("email") String email) {
        userService.deleteByEmail(email);
    }

    @RequestMapping(value = "/group", method = POST)
    public Collection<User> getGroup(@Param("group") String group) {
//      TODO make json projection
        return userService.findByGroupName(group);
    }

    @RequestMapping(value = "/pay", method = POST)
    public String pay(@Param("project") String project, @Param("email") String email,
                      @Param("sum") int sum, @Param("currency") Currency currency, @Param("comment") String comment,
                      @RequestParam(value = "type", required = false) ParticipationType participationType,
                      @RequestParam(value = "template", required = false) String template) {
        groupService.pay(email.toLowerCase(), project, new Payment(sum, currency, comment), participationType);
        return (template == null) ? "Paid" : mailService.sendToUser(template, email);
    }
}
