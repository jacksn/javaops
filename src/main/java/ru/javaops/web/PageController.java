package ru.javaops.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.javaops.model.User;
import ru.javaops.service.UserService;

import java.util.Set;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class PageController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/user_list", method = GET)
    public ModelAndView usersInfo() {
        Set<User> userSet = userService.findAgreeStatsUsers();
        return new ModelAndView("user_list", "users", userSet);
    }
}
