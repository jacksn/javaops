package ru.javaops.web;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.javaops.AuthorizedUser;
import ru.javaops.model.IdeaCoupon;
import ru.javaops.model.User;
import ru.javaops.repository.UserRepository;
import ru.javaops.service.*;
import ru.javaops.to.UserStat;
import ru.javaops.to.UserToExt;
import ru.javaops.util.UserUtil;
import ru.javaops.util.Util;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * gkislin
 * 19.05.2017
 */

@Controller
@Slf4j
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
    private RefService refService;

    @Autowired
    private IdeaCouponService ideaCouponService;

    @Autowired
    private GoogleAdminSDKDirectoryService googleAdminSDKDirectoryService;

    @GetMapping("/auth/profile")
    public ModelAndView profile() {
        return getProfileView(null);
    }

    @GetMapping("/auth/profileER")
    public ModelAndView profileER(@RequestParam(value = "project", required = false) String projectName) {
        return new ModelAndView("profileER", "project", projectName);
    }

    @RequestMapping(value = "/participate", method = RequestMethod.GET)
    public ModelAndView participate(@RequestParam("email") String email, @RequestParam("key") String key, @RequestParam("project") String projectName) {
        return getProfileView(ImmutableMap.of("project", projectName));
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ModelAndView profile(@RequestParam("email") String email, @RequestParam("key") String key) {
        return getProfileView(null);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public ResponseEntity<String> delete(@RequestParam("email") String email, @RequestParam("key") String key) {
        return new ResponseEntity<>(userService.deleteByEmail(email) ? "OK" : "Not Found", HttpStatus.OK);
    }

    @RequestMapping(value = "/auth/save", method = RequestMethod.POST)
    public ModelAndView save(@RequestParam(value = "project", required = false) String project, @Valid UserToExt userToExt, BindingResult result) {
        if (result.hasErrors()) {
            throw new ValidationException(Util.getErrorMessage(result));
        }
        User user = userService.update(userToExt);
        AuthorizedUser.updateUser(user);
        if (!Strings.isNullOrEmpty(project)) {
            String email = userToExt.getEmail();
            groupService.getExistedUserInCurrentProject(email, project);
            return grantAllAccess(email, userToExt.getGmail(), project);
        } else {
            return new ModelAndView("saveProfile");
        }
    }

    @GetMapping(value = "/auth/users")
    public ModelAndView usersInfo() {
        List<UserStat> users = userRepository.findAllForStats();
        return (users.stream().anyMatch(u -> u.getEmail().equals(AuthorizedUser.user().getEmail()))) ?
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
                        "accessResponse", accessResponse, "project", project));
    }

    private ModelAndView getProfileView(Map<String, ?> params) {
        User user = checkNotNull(AuthorizedUser.user());
        String aboutMe = UserUtil.normalize(user.getAboutMe());

        Map<String, String> projects = groupService.getGroupsByUserId(user.getId()).stream()
                .filter(g -> g.getProject() != null)
                .collect(Collectors.toMap(g -> g.getProject().getName(), g -> g.getType().name(), (t1, t2) -> t1 + t2));
/*
        Map<String, Set<GroupType>> projects = groupService.getGroupsByUserId(user.getId()).stream()
                .filter(g -> g.getProject() != null)
                .collect(Collectors.groupingBy(g -> g.getProject().getName(),
                        Collector.of(
                                () -> EnumSet.noneOf(GroupType.class),
                                (set, g) -> set.add(g.getType()),
                                (set, set2) -> {
                                    set.addAll(set2);
                                    return set;
                                })));
*/

        String refUrl = refService.getRefUrl(null, user.getEmail());

        String ideaCoupon = "";
        if (user.isMember()) {
            IdeaCoupon coupon = ideaCouponService.getByUser(user);
            if (coupon != null) {
                ideaCoupon = coupon.getCoupon();
            }
        }
        ImmutableMap.Builder<String, Object> builder =
                new ImmutableMap.Builder<String, Object>()
                        .put("aboutMe", aboutMe)
                        .put("refUrl", refUrl)
                        .put("ideaCoupon", ideaCoupon)
                        .put("projects", projects);

        if (params != null) {
            builder = builder.putAll(params);
        }
        return new ModelAndView("profile", builder.build());
    }
}