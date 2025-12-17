package org.nazarius.VovkORM.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * A simple DataSource implementation using DriverManager.
 * This class does not provide connection pooling.
 */
public class SimpleDataSource implements DataSource {
    private final String url;
    private final String username;
    private final String password;

    private PrintWriter logWriter;
    private int loginTimeout;

    public SimpleDataSource(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    @Override
    public PrintWriter getLogWriter() {
        return logWriter;
    }

    @Override
    public void setLogWriter(PrintWriter out) {
        this.logWriter = out;
        DriverManager.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) {
        this.loginTimeout = seconds;
        DriverManager.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() {
        return loginTimeout;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("Logging not supported");
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        if (aClass.isInstance(aClass)) {
            return aClass.cast(this);
        }
        throw new SQLException("Not a wrapper for " + aClass.getName());
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) {
        return aClass.isInstance(this);
    }
}
