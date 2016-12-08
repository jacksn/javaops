package ru.javaops.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.javaops.SqlResult;
import ru.javaops.config.AppConfig;
import ru.javaops.repository.SqlRepository;
import ru.javaops.service.UserService;
import ru.javaops.to.UserStat;

import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class PageController {
    private static final Logger LOG = LoggerFactory.getLogger(PageController.class);

    @Autowired
    private UserService userService;

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
    public ModelAndView sqlExecute(@RequestParam("sql_key") String sqlKey, @RequestParam Map<String, String> params) {
        String sql = AppConfig.SQL_PROPS.getProperty(sqlKey);
        if (sql == null) {
            throw new IllegalArgumentException("Key '" + sqlKey + "' is not found");
        }
        try {
            SqlResult result = sqlRepository.execute(sql, params);
            return new ModelAndView("sqlResult", "result", result);
        } catch (Exception e) {
            LOG.error("Sql '" + sql + "' execution exception", e);
            throw new IllegalStateException("Sql execution exception");
        }
    }
}
