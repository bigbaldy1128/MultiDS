package com.qihoo.multids.dbconfig;

/**
 * Created by wangjinzhao on 2017/7/4.
 */
public class DataSourceContextHolder {
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    public static void setDB(String dbName) {
        contextHolder.set(dbName);
    }

    static String getDB() {
        return (contextHolder.get());
    }
}
