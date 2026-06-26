package com.parrot.common;

/**
 * 当前请求线程里的登录用户，拦截器放入，用完及时清理。
 */
public class CurrentUserContext {

    private static final ThreadLocal<CurrentUser> USER_HOLDER = new ThreadLocal<>();

    private CurrentUserContext() {
    }

    public static void set(CurrentUser user) {
        USER_HOLDER.set(user);
    }

    public static CurrentUser get() {
        return USER_HOLDER.get();
    }

    public static Long getUserId() {
        CurrentUser user = get();
        return user == null ? null : user.getUserId();
    }

    public static void clear() {
        USER_HOLDER.remove();
    }
}
