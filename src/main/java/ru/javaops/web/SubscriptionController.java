package ru.javaops.web;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.javaops.AuthorizedUser;
import ru.javaops.model.*;
import ru.javaops.service.*;
import ru.javaops.to.UserMailImpl;
import ru.javaops.to.UserTo;
import ru.javaops.to.UserToExt;
import ru.javaops.util.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

/**
 * GKislin
 */
@Controller
@Slf4j
public class SubscriptionController {

    @Autowired
    private IntegrationService integrationService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private IdeaCouponService ideaCouponService;

    @RequestMapping(value = "/activate", method = RequestMethod.GET)
    public ModelAndView activate(@RequestParam("email") String email, @RequestParam("activate") boolean activate, @RequestParam("key") String key) {
        log.info("User {} set activete={}", email, activate);
        User u = userService.findByEmail(email);
        if (u != null && u.isActive() != activate) {
            u.setActive(activate);
            u.setActivatedDate(new Date());
            userService.save(u);
        }
        return new ModelAndView("activation",
                ImmutableMap.of("activate", activate,
                        "subscriptionUrl", subscriptionService.getSubscriptionUrl(email, key, !activate)));
    }

    @RequestMapping(value = "/register-group", method = RequestMethod.POST)
    public ModelAndView registerInGroup(@RequestParam("group") String group,
                                        @RequestParam(value = "confirmMail", required = false) String confirmMail,
                                        @RequestParam(value = "callback", required = false) String callback,
                                        @RequestParam("channel") String channel,
                                        @RequestParam(value = "template", required = false) String template,
                                        @RequestParam(value = "type", required = false) ParticipationType participationType,
                                        @RequestParam("channelKey") String channelKey,
                                        @Valid UserTo userTo, BindingResult result) {
        if (result.hasErrors()) {
            throw new ValidationException(Util.getErrorMessage(result));
        }
        UserGroup userGroup = groupService.registerAtGroup(userTo, group, channel, participationType);
        String mailResult = "без отправки";
        if (StringUtils.isNotEmpty(template)) {
            mailResult = mailService.sendWithTemplate(template, userGroup.getUser(), ImmutableMap.of("participationType", participationType == null ? "" : participationType));
        }
        ImmutableMap<String, ?> params = ImmutableMap.of("userGroup", userGroup, "result", mailResult);

        final ModelAndView mv;
        if (callback != null) {
            mv = getRedirectView(mailResult, callback, "error");
        } else {
            mv = new ModelAndView("simpleConfirm", params);
        }
        if (confirmMail != null) {
            mailService.sendWithTemplateAsync(new UserMailImpl(null, confirmMail), "simpleConfirm", params);
        }
        return mv;
    }

    @RequestMapping(value = "/register-site", method = RequestMethod.POST)
    public ModelAndView registerSite(@CookieValue(value = "channel", required = false) String cookieChannel,
                                     @CookieValue(value = "ref", required = false) String refUserId,
                                     HttpServletRequest request) {
        UserToExt userToExt = AuthorizedUser.getPreAuthorized(request);
        if (userToExt == null) {
            WebUtil.logWarn(request);
            return null;
        }
        log.info("+++ !!! Register from Site, {}", userToExt);
        User user = UserUtil.createFromToExt(userToExt);
        userService.save(user);
        AuthorizedUser.setAuthorized(user, request);
        return new ModelAndView("redirect:/auth/profile");
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView registerByProject(@RequestParam("project") String projectName,
                                          @RequestParam(value = "channel", required = false) String channel,
                                          @RequestParam(value = "template", required = false) String template,
                                          @Valid UserTo userTo, BindingResult result,
                                          @CookieValue(value = "channel", required = false) String cookieChannel,
                                          @CookieValue(value = "ref", required = false) String refUserId) {
        if (result.hasErrors()) {
            throw new ValidationException(Util.getErrorMessage(result));
        }
        User refUser = null;
        if (!Strings.isNullOrEmpty(refUserId)) {
            try {
                refUser = userService.get(Integer.parseInt(refUserId));
                if (refUser != null) {
                    channel = RefUtil.markRef(refUser.getEmail());
                } else {
                    channel = "Unknown_refUserId_" + refUserId;
                }
            } catch (Exception e) {
                channel = "Unknown_refUserId_" + refUserId;
            }
        } else if (!Strings.isNullOrEmpty(cookieChannel)) {
            channel = cookieChannel;
        }
        log.info("+++ !!! Register from '{}', project={}, email={}", channel, projectName, userTo.getEmail());

        UserGroup userGroup = groupService.registerAtProject(userTo, projectName, channel);
        if (userGroup.isAlreadyExist()) {
            Date date = userGroup.getRegisteredDate();
            if (date != null) {
                LocalDate ld = LocalDate.of(date.getYear() + 1900, date.getMonth() + 1, date.getDate());
                if (ld.isAfter(LocalDate.now().minus(15, ChronoUnit.DAYS))) {
                    return getRedirectView("/view/duplicate");
                }
            }
            userGroup.setRegisteredDate(new Date());
            groupService.save(userGroup);
        } else if (userGroup.getRegisterType() == RegisterType.REPEAT) {
            integrationService.asyncSendSlackInvitation(userGroup.getUser().getEmail(), projectName);
            template = projectName + "_repeat";
        } else {
            if (template == null) {
                template = projectName + "_register";
            }
            if (refUser != null) {
                mailService.sendRefMail(refUser, "ref/refRegistration", ImmutableMap.of("project", projectName, "email", userTo.getEmail()));
            }
        }
        String mailResult = mailService.sendToUser(template, userGroup.getUser());
        return getRedirectView(mailResult, "/view/confirm", "/view/error");
    }

    private ModelAndView getRedirectView(String mailResult, String successUrl, String failUrl) {
        return getRedirectView(MailService.isOk(mailResult) ? successUrl : failUrl);
    }

    private ModelAndView getRedirectView(String url) {
        return new ModelAndView("redirectToUrl", "redirectUrl", url);
    }

    @RequestMapping(value = "/repeat", method = RequestMethod.GET)
    public ModelAndView repeat(@RequestParam("email") String email,
                               @RequestParam("project") String projectName) throws MessagingException {

        email = email.toLowerCase();
        User user = userService.findExistedByEmail(email);
        Set<Group> groups = groupService.getGroupsByUserId(user.getId());

        Optional<Group> optGroup = ProjectUtil.getGroupByProjectAndType(groups, projectName, GroupType.CURRENT);
        if (optGroup.isPresent()) {
            return new ModelAndView("already_registered", "group", optGroup.get().getName());
        }
        if (ProjectUtil.getGroupByProjectAndType(groups, projectName, GroupType.FINISHED).isPresent()) {
            ProjectUtil.ProjectProps projectProps = groupService.getProjectProps(projectName);
            groupService.save(new UserGroup(user, projectProps.currentGroup, RegisterType.REPEAT, "repeat"));

            mailService.sendToUser(projectName + "_repeat", user);
            IntegrationService.SlackResponse response = integrationService.sendSlackInvitation(email, projectName);
            return new ModelAndView("registration",
                    ImmutableMap.of("response", response, "email", email, "project", projectName));
        }
        throw new IllegalStateException("Пользователь <b>" + email + "</b> не участник проекта " + projectName);
    }

    @RequestMapping(value = "/idea", method = RequestMethod.GET)
    public ModelAndView ideaRegister(@RequestParam("email") String email, @RequestParam("project") String projectName) throws MessagingException {
        User user = userService.findExistedByEmail(email);
        checkState(groupService.isProjectMember(user.getId(), projectName), "Пользователь %s не найден в проекте %s", email, projectName);

        IdeaCoupon coupon = ideaCouponService.assignToUser(user, groupService.getProjectProps(projectName).project);
        String response = mailService.sendWithTemplate("idea_register", user, ImmutableMap.of("coupon", coupon.getCoupon()));
        if (MailService.OK.equals(response)) {
            return new ModelAndView("registration_idea");
        } else {
            throw new IllegalStateException("Ошибка отправки почты" + response);
        }
    }
}