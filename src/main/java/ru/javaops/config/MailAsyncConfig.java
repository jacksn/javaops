package ru.javaops.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;

/**
 * GKislin
 * 04.02.2016
 */

@EnableAsync
@EnableScheduling
@Configuration
public class MailAsyncConfig implements AsyncConfigurer {
    private static final Logger log = LoggerFactory.getLogger(MailAsyncConfig.class);

    @Autowired
    private Environment env;

    @Override
    @Bean(name = "mailExecutor")
    public Executor getAsyncExecutor() {
        log.info("Creating Async TaskExecutor");
        return new ThreadPoolTaskExecutor() {
            {
                setCorePoolSize(env.getProperty("mail.async.corePoolSize", Integer.class));
                setMaxPoolSize(env.getProperty("mail.async.maxPoolSize", Integer.class));
                setQueueCapacity(env.getProperty("mail.async.queueCapacity", Integer.class));
                setThreadNamePrefix("mail-async-executor-");
            }
        };
    }

    @Bean(name = "mailCompletionService")
//    http://docs.spring.io/spring-javaconfig/docs/1.0.0.M4/reference/html/ch04s02.html
    public CompletionService getCompletionService() {
        log.info("Creating CompletionService");
        return new ExecutorCompletionService(getAsyncExecutor());
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}
