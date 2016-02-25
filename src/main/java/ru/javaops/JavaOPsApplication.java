package ru.javaops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.DispatcherServlet;
import ru.javaops.config.AppProperties;
import ru.javaops.config.IntegrationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AppProperties.class, IntegrationProperties.class})
@EnableJpaRepositories("ru.javaops.repository")
@EnableCaching
public class JavaOPsApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(JavaOPsApplication.class, args);
        DispatcherServlet dispatcherServlet = ctx.getBean("dispatcherServlet", DispatcherServlet.class);
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
    }
}
