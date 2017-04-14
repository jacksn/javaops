package ru.javaops.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import ru.javaops.service.SubscriptionService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

//@EnableWebMvc : http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-spring-mvc-auto-configuration
@EnableAutoConfiguration // OK, 10 converters
@Configuration // OK, 9 converters
public class MvcConfiguration extends WebMvcConfigurerAdapter {

/*
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("./i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(properties.getCacheSeconds());
        return messageSource;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("language");
        registry.addInterceptor(localeChangeInterceptor);
    }
*/

    @Autowired
    private SubscriptionService subscriptionService;

    @Bean
    public HandlerInterceptor activationKeyInterceptor() {
        return new HandlerInterceptorAdapter() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                check(request, "adminKey", adminKey -> subscriptionService.checkAdminKey(adminKey));
                check(request, "channelKey", channelKey -> {
                    String channel = request.getParameter("channel");
                    subscriptionService.checkActivationKey(checkNotNull(channel, "Не задан channel"), channelKey);
                });
                check(request, "key", key -> {
                    String email = request.getParameter("email");
                    subscriptionService.checkActivationKey(checkNotNull(email, "Не задан email"), key);
                });
                return true;
            }
        };
    }

    private void check(HttpServletRequest request, String param, Consumer<String> checker) {
        String value = request.getParameter(param);
        if (value != null) {
            checker.accept(value);
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(activationKeyInterceptor());
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("file:./resources/static/");
        registry.addResourceHandler("/css/**").addResourceLocations("file:./resources/css/");
        registry.addResourceHandler("/*.html").addResourceLocations("file:./resources/");
        registry.addResourceHandler("/**/*.html").addResourceLocations("file:./resources/");
    }
}
