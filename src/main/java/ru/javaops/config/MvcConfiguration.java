package ru.javaops.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.UrlFilenameViewController;
import ru.javaops.AuthorizedUser;
import ru.javaops.service.SubscriptionService;
import ru.javaops.service.UserService;
import ru.javaops.util.WebUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Properties;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

//@EnableWebMvc : http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-spring-mvc-auto-configuration
@EnableAutoConfiguration
@Configuration
@Slf4j
public class MvcConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private UserService userService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptorAdapter() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                String project = request.getParameter("project");
                if (project != null && StringUtils.isBlank(project)) {
                    WebUtil.logWarn(request);
                    return false;
                }
                check(request, "adminKey", adminKey -> subscriptionService.checkAdminKey(adminKey));
                check(request, "channelKey", channelKey -> {
                    String channel = request.getParameter("channel");
                    subscriptionService.checkActivationKey(checkNotNull(channel, "Не задан channel"), channelKey);
                });
                check(request, "key", key -> {
                    String email = request.getParameter("email");
                    subscriptionService.checkActivationKey(checkNotNull(email, "Не задан email"), key);
                    AuthorizedUser.setAuthorized(userService.findExistedByEmail(email), request);
                });
                return true;
            }
        }).excludePathPatterns("/api/**");
        registry.addInterceptor(authInterceptor()).excludePathPatterns("/api/**");
    }

    private void check(HttpServletRequest request, String param, Consumer<String> checker) {
        String value = request.getParameter(param);
        if (value != null) {
            checker.accept(value);
        }
    }

    @Bean
    public HandlerInterceptor authInterceptor() {
        return new HandlerInterceptorAdapter() {
            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
                if (modelAndView != null && !modelAndView.isEmpty()) {
                    modelAndView.getModelMap().addAttribute("authUser", AuthorizedUser.user());
                }
            }
        };
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/interview/test.html").setViewName("test");
        registry.addViewController("/payment.html").setViewName("payment");
        registry.addViewController("/story.html").setViewName("story");
    }

    //  http://www.codejava.net/frameworks/spring/spring-mvc-url-based-view-resolution-with-urlfilenameviewcontroller-example
    @Bean
    public SimpleUrlHandlerMapping getUrlHandlerMapping() {
        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setMappings(new Properties() {
            {
//                https://stackoverflow.com/a/12569566/548473
                put("/view/**", new UrlFilenameViewController());
            }
        });
        handlerMapping.setOrder(Integer.MAX_VALUE - 5);
        handlerMapping.setInterceptors(authInterceptor());
        return handlerMapping;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("file:./resources/static/");
        registry.addResourceHandler("/css/**").addResourceLocations("file:./resources/css/");
        registry.addResourceHandler("/*.html", "/**/*.html").addResourceLocations("file:./resources/");
        registry.setOrder(Integer.MAX_VALUE);
    }
}
