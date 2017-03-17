package ru.javaops.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.javaops.SqlResult;
import ru.javaops.config.AppConfig;
import ru.javaops.repository.SqlRepository;
import ru.javaops.repository.UserRepository;
import ru.javaops.service.SubscriptionService;
import ru.javaops.to.UserStat;

import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@Slf4j
public class PageController {

    public static final String PARTNER_GROUP_NAME = "partner";
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private SqlRepository sqlRepository;

    @RequestMapping(value = "/users", method = GET)
    public ModelAndView usersInfo(@RequestParam("key") String key, @RequestParam("email") String email) {
        List<UserStat> users = userRepository.findAllForStats();
        return (users.stream().anyMatch(u -> u.getEmail().equals(email))) ?
                new ModelAndView("users", "users", users) :
                new ModelAndView("statsForbidden");
    }

    @RequestMapping(value = "/sql", method = GET)
    public ModelAndView sqlExecute(@RequestParam("sql_key") String sqlKey,
                                   @RequestParam(value = "limit", required = false) Integer limit,
                                   @RequestParam("partnerKey") String partnerKey,
                                   @RequestParam Map<String, String> params) {

        if (!subscriptionService.checkSecret(partnerKey) &&
                userRepository.findByEmailAndGroupName(partnerKey.toLowerCase(), PARTNER_GROUP_NAME) == null) {
            return new ModelAndView("noRegisteredHR", "email", partnerKey);
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
