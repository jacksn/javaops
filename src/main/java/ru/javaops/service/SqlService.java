package ru.javaops.service;

import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javaops.SqlResult;
import ru.javaops.config.AppConfig;
import ru.javaops.model.User;
import ru.javaops.repository.SqlRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * gkislin
 * 10.04.2017
 */
@Service
@Slf4j
public class SqlService {

    @Autowired
    private SqlRepository sqlRepository;

    public SqlResult execute(String sqlKey, Integer limit, Map<String, ?> params) {
        return execute(sqlKey, sql -> {
            if (limit != null) {
                sql = sql.replace(":limit", String.valueOf(limit));
            }
            return sqlRepository.execute(sql, params);
        });
    }

    public Set<User> getUsers(String sqlKey) {
        return execute(sqlKey, sql -> {
            List<User> users = sqlRepository.getUsers(sql);
            return ImmutableSet.copyOf(users);
        });
    }

    private <T> T execute(String sqlKey, Function<String, T> sqlExecutor) {
        String sql = AppConfig.SQL_PROPS.getProperty(sqlKey);
        if (sql == null) {
            throw new IllegalArgumentException("Key '" + sqlKey + "' is not found");
        }
        try {
            return sqlExecutor.apply(sql);
        } catch (Exception e) {
            log.error("Sql '" + sql + "' execution exception", e);
            throw new IllegalStateException("Sql execution exception");
        }
    }
}