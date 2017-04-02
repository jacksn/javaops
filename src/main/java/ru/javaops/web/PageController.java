package ru.javaops.web;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.javaops.SqlResult;
import ru.javaops.config.AppConfig;
import ru.javaops.model.Group;
import ru.javaops.model.Project;
import ru.javaops.model.User;
import ru.javaops.repository.SqlRepository;
import ru.javaops.repository.UserRepository;
import ru.javaops.service.CachedGroups;
import ru.javaops.to.UserStat;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@Slf4j
public class PageController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SqlRepository sqlRepository;

    @Autowired
    private CachedGroups cachedGroups;

    @RequestMapping(value = "/users", method = GET)
    public ModelAndView usersInfo(@RequestParam("key") String key, @RequestParam("email") String email) {
        List<UserStat> users = userRepository.findAllForStats();
        return (users.stream().anyMatch(u -> u.getEmail().equals(email))) ?
                new ModelAndView("users", "users", users) :
                new ModelAndView("statsForbidden");
    }

    @RequestMapping(value = "/user", method = GET)
    public ModelAndView userInfo(@RequestParam("email") String email,
                                 @RequestParam("partnerKey") String partnerKey) {

        User user = userRepository.findByEmailWithGroup(email);
        Map<Integer, Group> groupMembers = cachedGroups.getMembers();
        List<Project> projects = user.getUserGroups().stream()
                .filter(ug -> groupMembers.containsKey(ug.getGroup().getId()))
                .map(ug -> groupMembers.get(ug.getGroup().getId()).getProject())
                .distinct()
                .collect(Collectors.toList());
        return new ModelAndView("userInfo", ImmutableMap.of("user", user, "projects", projects));
    }

    @RequestMapping(value = "/sql", method = GET)
    public ModelAndView sqlExecute(@RequestParam("sql_key") String sqlKey,
                                   @RequestParam(value = "limit", required = false) Integer limit,
                                   @RequestParam(value = "csv", required = false, defaultValue = "false") boolean csv,
                                   @RequestParam("partnerKey") String partnerKey,
                                   @RequestParam Map<String, String> params) {

        String sql = AppConfig.SQL_PROPS.getProperty(sqlKey);
        if (sql == null) {
            throw new IllegalArgumentException("Key '" + sqlKey + "' is not found");
        }
        try {
            if (limit != null) {
                sql = sql.replace(":limit", String.valueOf(limit));
            }
            params.put("partnerKey", partnerKey);
            SqlResult result = sqlRepository.execute(sql, params);
            return new ModelAndView("sqlResult",
                    ImmutableMap.of("result", result, "csv", csv));
        } catch (Exception e) {
            log.error("Sql '" + sql + "' execution exception", e);
            throw new IllegalStateException("Sql execution exception");
        }
    }
}
