package ru.javaops.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.javaops.service.UserService;
import ru.javaops.to.UserStat;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class PageController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/users", method = GET)
    public ModelAndView usersInfo(@RequestParam("key") String key, @RequestParam("email") String email) {
        List<UserStat> userList = userService.findAllForStats();
        return (userList.stream().filter(u -> u.getEmail().equals(email)).findAny().isPresent()) ?
                new ModelAndView("userList", "users", userList) :
                new ModelAndView("statsForbidden");
    }
}
