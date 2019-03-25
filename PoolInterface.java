package com.springboot.Pool;

import java.sql.Connection;

/**
 * 配置接口
 * */
public interface PoolInterface {
    public static final String driver = "com.mysql.jdbc.Driver";
    public static final String url = "jdbc:mysql:///myproject?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&useSSL=true";
    public static final String username = "root";
    public static final String password = "yang";

    /**
     * 获取链接
     * */
    public Connection getConnection() throws Throwable;
}
