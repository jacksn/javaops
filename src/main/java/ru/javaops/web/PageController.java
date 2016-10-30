package ru.javaops.web;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.javaops.SqlResult;
import ru.javaops.service.SqlService;
import ru.javaops.service.UserService;
import ru.javaops.to.UserStat;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class PageController {

    @Autowired
    private UserService userService;

    @Autowired
    private SqlService sqlService;

    @RequestMapping(value = "/users", method = GET)
    public ModelAndView usersInfo(@RequestParam("key") String key, @RequestParam("email") String email) {
        List<UserStat> users = userService.findAllForStats();
        return (users.stream().filter(u -> u.getEmail().equals(email)).findAny().isPresent()) ?
                new ModelAndView("users", "users", users) :
                new ModelAndView("statsForbidden");
    }

    @RequestMapping(value = "/sql", method = GET)
    public ModelAndView sqlExecute(@RequestParam("sql_key") String sqlKey) {
        SqlResult result = sqlService.executeSql(sqlKey, ImmutableMap.of());
        return new ModelAndView("sqlResult", "result", result);
    }
}
