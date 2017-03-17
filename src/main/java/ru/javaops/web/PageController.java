package ru.javaops.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.javaops.SqlResult;
import ru.javaops.config.AppConfig;
import ru.javaops.model.User;
import ru.javaops.repository.SqlRepository;
import ru.javaops.service.GroupService;
import ru.javaops.service.SubscriptionService;
import ru.javaops.service.UserService;
import ru.javaops.to.UserStat;

import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@Slf4j
public class PageController {

    public static final String HR_GROUP_NAME = "hr";
    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private SqlRepository sqlRepository;

    @RequestMapping(value = "/users", method = GET)
    public ModelAndView usersInfo(@RequestParam("key") String key, @RequestParam("email") String email) {
        List<UserStat> users = userService.findAllForStats();
        return (users.stream().anyMatch(u -> u.getEmail().equals(email))) ?
                new ModelAndView("users", "users", users) :
                new ModelAndView("statsForbidden");
    }

    @RequestMapping(value = "/sql", method = GET)
    public ModelAndView sqlExecute(@RequestParam("sql_key") String sqlKey,
                                   @RequestParam(value = "limit", required = false) Integer limit,
                                   @RequestParam Map<String, String> params,
                                   @RequestParam("email") String email) {

        User user = userService.findByEmailAndGroupId(email, groupService.findByName(HR_GROUP_NAME).getId());
        if (user == null && !subscriptionService.checkSecret(email)) {
            return new ModelAndView("noRegisteredHR", "email", email);
        }
        String sql = AppConfig.SQL_PROPS.getProperty(sqlKey);
        if (sql == null) {
            throw new IllegalArgumentException("Key '" + sqlKey + "' is not found");
        }
        try {
            if (limit != null) {
                sql = sql.replace(":limit", String.valueOf(limit));
            }
            SqlResult result = sqlRepository.execute(sql, params);
            return new ModelAndView("sqlResult", "result", result);
        } catch (Exception e) {
            log.error("Sql '" + sql + "' execution exception", e);
            throw new IllegalStateException("Sql execution exception");
        }
    }
}
