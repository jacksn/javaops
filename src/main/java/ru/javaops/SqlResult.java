package ru.javaops;

import java.util.List;

/**
 * gkislin
 * 30.10.2016
 */
public class SqlResult {
    public SqlResult(List<String> headers, List<Object[]> rows) {
        this.headers = headers;
        this.rows = rows;
    }

    public List<String> headers;
    public List<Object[]> rows;
}
