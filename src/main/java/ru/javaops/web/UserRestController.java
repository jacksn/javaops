package ru.javaops.web;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.javaops.model.*;
import ru.javaops.service.GroupService;
import ru.javaops.service.MailService;
import ru.javaops.service.RefService;
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
@Slf4j
public class UserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private RefService refService;

    @Autowired
    private MailService mailService;

    @Autowired
    private GroupService groupService;

    @RequestMapping(method = DELETE)
    public void delete(@RequestParam("email") String email) {
        userService.deleteByEmail(email);
    }

    @RequestMapping(value = "/pay", method = POST)
    public String pay(@RequestParam("group") String group, @Valid UserTo userTo,
                      @RequestParam("sum") int sum, @RequestParam("currency") Currency currency, @RequestParam("comment") String comment,
                      @RequestParam(value = "type", required = false) ParticipationType participationType,
                      @RequestParam(value = "channel", required = false) String channel,
                      @RequestParam(value = "template", required = false) String template) {
        UserGroup ug = groupService.pay(userTo, group, new Payment(sum, currency, comment), participationType, channel);
        User refUser = null;
        if (ug.isAlreadyExist()) {
            log.info("User {} already exist in {}", userTo.getEmail(), group);
        } else {
            refUser = refService.getRefUser(ug.getChannel());
            if (refUser != null) {
                refUser.addBonus(25);
                log.info("!!! Ref Participation from user {}, bonus {}", refUser.getEmail(), refUser.getBonus());
                userService.save(refUser);
                mailService.sendRefMail(refUser, "ref/refParticipation", ImmutableMap.of("group", group, "email", userTo.getEmail()));
            }
        }
        return (refUser == null ? "" : "Reference from " + refUser.getEmail() + ", bonus=" + refUser.getBonus() + "\n") +
                ug.toString() + '\n' + (template == null ? "No template" : mailService.sendToUser(template, ug.getUser()));
    }
}