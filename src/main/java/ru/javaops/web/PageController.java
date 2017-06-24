package ru.javaops.web;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.javaops.SqlResult;
import ru.javaops.model.Group;
import ru.javaops.model.Project;
import ru.javaops.model.User;
import ru.javaops.repository.UserRepository;
import ru.javaops.service.CachedGroups;
import ru.javaops.service.RefService;
import ru.javaops.service.SqlService;
import ru.javaops.service.SubscriptionService;
import ru.javaops.to.UserAdminsInfo;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class PageController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SqlService sqlService;

    @Autowired
    private CachedGroups cachedGroups;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private RefService refService;

    @GetMapping(value = "/user")
    public ModelAndView userInfo(@RequestParam("email") String email,
                                 @RequestParam("partnerKey") String partnerKey) {

        User partner = subscriptionService.checkPartner(partnerKey);
        User user = userRepository.findByEmailWithGroup(email);
        Map<Integer, Group> groupMembers = cachedGroups.getMembers();
        List<Project> projects = user.getUserGroups().stream()
                .filter(ug -> groupMembers.containsKey(ug.getGroup().getId()))
                .map(ug -> groupMembers.get(ug.getGroup().getId()).getProject())
                .distinct()
                .collect(Collectors.toList());
        return new ModelAndView("userInfo",
                ImmutableMap.of("user", user, "projects", projects, "partner", partner));
    }

    @PostMapping(value = "/saveAdminInfo")
    public String saveComment(@RequestParam("email") String email,
                              @RequestParam("adminKey") String adminKey,
                              UserAdminsInfo uaInfo) {
//        userRepository.saveAdminInfo(email, uaInfo);
        userRepository.saveAdminInfo(email, uaInfo.getComment(), uaInfo.getMark(), uaInfo.getBonus());
        return "closeWindow";
    }

    @GetMapping(value = "/sql")
    public ModelAndView sqlExecute(@RequestParam("sql_key") String sqlKey,
                                   @RequestParam(value = "limit", required = false) Integer limit,
                                   @RequestParam(value = "csv", required = false, defaultValue = "false") boolean csv,
                                   @RequestParam("partnerKey") String partnerKey,
                                   @RequestParam(value = "fromDate", required = false) String fromDate,
                                   @RequestParam Map<String, String> params) {

        User partner = subscriptionService.checkPartner(partnerKey);
        params.put("partnerKey", partnerKey);
        params.put("partnerMark", partner.getMark());
        params.put("fromDate", fromDate == null ? "01-01-01" : fromDate);
        params.put("toDate", fromDate == null ? "3000-01-01" : DateTimeFormatter.ISO_DATE.format(LocalDate.now()));
        SqlResult result = sqlService.execute(sqlKey, limit, params);
        return new ModelAndView("sqlResult",
                ImmutableMap.of("result", result, "csv", csv));
    }

    @GetMapping(value = "/ref/{channel}")
    public ModelAndView rootReference(@PathVariable(value = "channel") String channel, HttpServletResponse response) {
        setCookie(response, channel, "root");
        return new ModelAndView("redirectToUrl", "redirectUrl", "/");
    }

    @GetMapping(value = "/ref/{project}/{channel}")
    public ModelAndView projectReference(@PathVariable(value = "channel") String channel,
                                         @PathVariable(value = "project") String project,
                                         HttpServletResponse response) {
        setCookie(response, channel, project);
        return new ModelAndView("redirectToUrl", "redirectUrl", "/reg/" + project);
    }

    @GetMapping(value = "/reg/{project}")
    public ModelAndView registration(@PathVariable(value = "project") String project,
                                     @RequestParam(value = "ch", required = false) String channel,
                                     HttpServletResponse response) {
        setCookie(response, "channel", channel, project);
        return new ModelAndView(project);
    }

    private void setCookie(HttpServletResponse response, String channel, String entry) {
        User user = refService.decryptUser(channel);
        if (user == null) {
            setCookie(response, "channel", channel, entry);
        } else {
            log.info("+++ Reference from user {}", user.getEmail());
            setCookie(response, "ref", user.getId().toString(), entry);
        }
    }

    private void setCookie(HttpServletResponse response, String name, String value, String entry) {
        if (value != null) {
            log.info("+++ set Cookie '{}' : '{}' for entry {}", name, value, entry);
            Cookie cookie = new Cookie(name, value);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 30); // 30 days
            response.addCookie(cookie);
        }
    }
}