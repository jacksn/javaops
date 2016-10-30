package ru.javaops.service;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javaops.SqlResult;
import ru.javaops.repository.SqlRepository;

import java.util.Map;

/**
 * gkislin
 * 30.10.2016
 */
@Service
public class SqlService {

    @Autowired
    private SqlRepository sqlRepository;

    public SqlResult executeSql(String sqlKey, Map<String, String> params) {
        if ("itedu".equals(sqlKey)) {
            return sqlRepository.execute(
                    "SELECT u.EMAIL, FORMATDATETIME(u.REGISTERED_DATE, 'yyyy-MM-dd') date FROM USERS u " +
                            "JOIN USER_GROUP ug ON u.id=ug.USER_ID AND ug.CHANNEL='itedu' " +
                            "ORDER BY date DESC",
                    ImmutableMap.of());
        }
        throw new IllegalStateException("Illegal Sql Key");
    }
}
