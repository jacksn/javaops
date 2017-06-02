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
import ru.javaops.service.SqlService;
import ru.javaops.service.SubscriptionService;
import ru.javaops.to.UserAdminsInfo;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
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
                                   @RequestParam Map<String, String> params) {

        User partner = subscriptionService.checkPartner(partnerKey);
        params.put("partnerKey", partnerKey);
        params.put("partnerMark", partner.getMark());
        SqlResult result = sqlService.execute(sqlKey, limit, params);
        return new ModelAndView("sqlResult",
                ImmutableMap.of("result", result, "csv", csv));
    }

    @GetMapping(value = "/ref/{project}/{channel}")
    public ModelAndView reference(@PathVariable(value = "channel") String channel,
                                          @PathVariable(value = "project") String project,
                                          HttpServletResponse response) {

        User user = subscriptionService.decryptUser(channel);
        Cookie cookie;
        if (user == null) {
            log.info("+++ Visit from channel {}", channel);
            cookie = new Cookie("channel", channel);
        } else {
            log.info("+++ Visit from user {}", user.getEmail());
            cookie = new Cookie("ref", user.getId().toString());
        }
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 30); // 30 days
        response.addCookie(cookie);
        return new ModelAndView("redirectToUrl", "redirectUrl", "/reg/" + project);
    }

    @GetMapping(value = "/reg/{project}")
    public ModelAndView registration(@PathVariable(value = "project") String project) {
        return new ModelAndView(project);
    }
}