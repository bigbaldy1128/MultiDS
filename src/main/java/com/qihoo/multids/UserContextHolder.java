package com.qihoo.multids;

/**
 * Created by wangjinzhao on 2017/10/27.
 */
class UserContextHolder {
    private static final ThreadLocal<Long> contextHolder = new ThreadLocal<>();

    static void setUserId(Long userId) {
        contextHolder.set(userId);
    }

    static Long getUserId() {
        return (contextHolder.get());
    }
}
