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
import ru.javaops.model.*;
import ru.javaops.service.*;
import ru.javaops.to.UserMail;
import ru.javaops.to.UserTo;
import ru.javaops.util.ProjectUtil;
import ru.javaops.util.Util;

import javax.mail.MessagingException;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Date;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

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
        User u = userService.findExistedByEmail(email);
        if (u.isActive() != activate) {
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
            mailResult = mailService.sendToUser(template, userGroup.getUser());
        }
        ImmutableMap<String, ?> params = ImmutableMap.of("userGroup", userGroup, "result", mailResult);

        final ModelAndView mv;
        if (callback != null) {
            mv = getRedirectView(mailResult, callback, "/error.html");
        } else {
            mv = new ModelAndView("confirm", params);
        }
        if (confirmMail != null) {
            mailService.sendWithTemplateAsync(confirmMail, "confirm", params);
        }
        return mv;
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
        if (!Strings.isNullOrEmpty(refUserId)) {
            User refUser = userService.get(Integer.parseInt(refUserId));
            channel = SubscriptionService.markRef(refUser.getEmail());
        } else if (!Strings.isNullOrEmpty(cookieChannel)) {
            channel = cookieChannel;
        }
        log.info("+++ !!! Register from '{}', project={}, email={}", channel, projectName, userTo.getEmail());

        UserGroup userGroup = groupService.registerAtProject(userTo, projectName, channel);

        // TODO send registration ref email
        if (userGroup.isAlreadyExist()) {
            return getRedirectView("/duplicate.html");
        } else if (userGroup.getRegisterType() == RegisterType.REPEAT) {
            integrationService.asyncSendSlackInvitation(userGroup.getUser().getEmail(), projectName);
            template = projectName + "_repeat";
        } else if (template == null) {
            template = projectName + "_register";
        }
        String mailResult = mailService.sendToUser(template, userGroup.getUser());
        return getRedirectView(mailResult, "/confirm.html", "/error.html");
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
        Set<Group> groups = groupService.findByUserId(user.getId());

        if (ProjectUtil.getGroupByProjectAndType(groups, projectName, GroupType.CURRENT).isPresent()) {
            return new ModelAndView("already_registered");
        }
        if (ProjectUtil.getGroupByProjectAndType(groups, projectName, GroupType.FINISHED).isPresent()) {
            ProjectUtil.ProjectProps projectProps = groupService.getProjectProps(projectName);
            groupService.save(user, projectProps.currentGroup, RegisterType.REPEAT, "repeat");

            mailService.sendToUser(projectName + "_repeat", user);
            IntegrationService.SlackResponse response = integrationService.sendSlackInvitation(email, projectName);
            return new ModelAndView("registration",
                    ImmutableMap.of("response", response, "email", email,
                            "activationKey", subscriptionService.generateActivationKey(email)));
        }
        throw new IllegalStateException("Пользователь " + email + " не участник проекта " + projectName);
    }

    @RequestMapping(value = "/idea", method = RequestMethod.GET)
    public ModelAndView ideaRegister(@RequestParam("email") String email, @RequestParam("project") String projectName) throws MessagingException {
        ProjectUtil.ProjectProps projectProps = groupService.getProjectProps(projectName);
        User user = userService.findByEmailAndGroupId(email, projectProps.currentGroup.getId());
        checkNotNull(user, "Пользователь %s не найден в проекте %s", email, projectName);

        IdeaCoupon coupon = ideaCouponService.assignToUser(user, projectProps.project);
        String response = mailService.sendWithTemplate("idea_register", new UserMail(user), ImmutableMap.of("coupon", coupon.getCoupon()));
        if (MailService.OK.equals(response)) {
            return new ModelAndView("registration_idea");
        } else {
            throw new IllegalStateException("Ошибка отправки почты" + response);
        }
    }
}