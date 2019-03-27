package com.springboot.Pool;

import java.sql.Connection;

/**
 * 配置接口
 * */
public interface PoolInterface {

    /**
     * 获取链接
     * */
    public Connection getConnection() throws Throwable;
}
