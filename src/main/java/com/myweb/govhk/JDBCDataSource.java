package com.myweb.govhk;

import org.apache.tomcat.jdbc.pool.DataSource;

import java.beans.PropertyVetoException;

public class JDBCDataSource {
    private static String driveClassName = "com.mysql.jdbc.Driver";
    private static String url = "jdbc:mysql://localhost:3306/pachong?useUnicode=true&characterEncoding=utf8&useSSL=false";
    private static String user = "root";
    private static String password = "root";
    private static DataSource dataSource = null;


    public static DataSource dataSource() throws PropertyVetoException {
        DataSource dataSource = new DataSource();
        dataSource.setDriverClassName(driveClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setJmxEnabled(true);
        dataSource.setTestWhileIdle(false);
        dataSource.setTestOnBorrow(true);
        dataSource.setValidationInterval(30000);
        dataSource.setTestOnReturn(false);
        dataSource.setValidationQuery("select 1");
        dataSource.setTimeBetweenEvictionRunsMillis(30000);
        dataSource.setMaxActive(100);
        dataSource.setInitialSize(10);
        dataSource.setMaxWait(10000);
        dataSource.setRemoveAbandonedTimeout(60);
        dataSource.setMinEvictableIdleTimeMillis(30000);
        dataSource.setMinIdle(10);
        dataSource.setLogAbandoned(true);
        dataSource.setRemoveAbandoned(true);
        return dataSource;
    }

    public static DataSource getDataSource() {
        if (dataSource == null) try {
            dataSource = dataSource();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        return dataSource;
    }
}
