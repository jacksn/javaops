package ru.javaops.web;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.javaops.AuthorizedUser;
import ru.javaops.model.User;
import ru.javaops.repository.UserRepository;
import ru.javaops.service.GoogleAdminSDKDirectoryService;
import ru.javaops.service.GroupService;
import ru.javaops.service.IntegrationService;
import ru.javaops.service.UserService;
import ru.javaops.to.UserStat;
import ru.javaops.to.UserToExt;
import ru.javaops.util.Util;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;

/**
 * gkislin
 * 19.05.2017
 */

@Controller
@Slf4j
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupService groupService;

    @Autowired
    private IntegrationService integrationService;

    @Autowired
    private GoogleAdminSDKDirectoryService googleAdminSDKDirectoryService;

    @GetMapping(value = "/participate")
    public ModelAndView participate(@RequestParam("project") String projectName) {
        User u = groupService.getExistedUserInProject(AuthorizedUser.get().getUsername(), projectName);
        return new ModelAndView("profile", ImmutableMap.of("user", u, "projectName", projectName));
    }

    @GetMapping
    public ModelAndView profile() {
        User u = userService.findExistedByEmail(AuthorizedUser.get().getUsername());
        return new ModelAndView("profile", ImmutableMap.of("user", u));
    }

    @PostMapping(value = "/save")
    public ModelAndView save(@RequestParam(value = "project", required = false) String project, @Valid UserToExt userToExt, BindingResult result) {
        if (result.hasErrors()) {
            throw new ValidationException(Util.getErrorMessage(result));
        }
        userService.update(userToExt);
        if (!Strings.isNullOrEmpty(project)) {
            String email = userToExt.getEmail();
            groupService.getExistedUserInProject(email, project);
            return grantAllAccess(email, userToExt.getGmail(), project);
        } else {
            return new ModelAndView("saveProfile", ImmutableMap.of("userToExt", userToExt));
        }
    }

    @GetMapping(value = "/users")
    public ModelAndView usersInfo() {
        List<UserStat> users = userRepository.findAllForStats();
        return (users.stream().anyMatch(u -> u.getEmail().equals(AuthorizedUser.get().getUsername()))) ?
                new ModelAndView("users", "users", users) :
                new ModelAndView("statsForbidden");
    }

    private ModelAndView grantAllAccess(String email, String gmail, String project) {
        log.info("grantAllAccess to {}/{}", email, gmail);
        IntegrationService.SlackResponse response = integrationService.sendSlackInvitation(email, project);
        String accessResponse = "";
        if (!project.equals("javaops")) {
            accessResponse = googleAdminSDKDirectoryService.insertMember(project + "@javaops.ru", gmail);
        }
        return new ModelAndView("registration",
                ImmutableMap.of("response", response, "email", email,
                        "accessResponse", accessResponse));
    }
}