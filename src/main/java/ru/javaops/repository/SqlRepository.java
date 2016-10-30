package ru.javaops.repository;

import org.parboiled.common.ImmutableList;
import org.springframework.stereotype.Repository;
import ru.javaops.SqlResult;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Map;

/**
 * gkislin
 * 30.10.2016
 */

@Repository
public class SqlRepository {
    @PersistenceContext
    private EntityManager em;

    public SqlResult execute(String sql, Map<Integer, Object> params) {
/*
        Session session = em.unwrap(Session.class);
        session.doWork((Connection connection)-> {
            PreparedStatement ps = connection.prepareStatement(sql);
            params.forEach((idx, value)->ps.setObject(idx, value));
        });
*/
        Query query = em.createNativeQuery(sql);
        return new SqlResult(ImmutableList.of("email, date"), query.getResultList());
    }
}
