package org.bigdata.server.config;

/**
 * 当前登录用户上下文（ThreadLocal）
 */
public class AuthContext {

    private static final ThreadLocal<Integer> USER_ID_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> USER_NAME_HOLDER = new ThreadLocal<>();

    public static void setUserId(Integer userId) {
        USER_ID_HOLDER.set(userId);
    }

    public static Integer getUserId() {
        return USER_ID_HOLDER.get();
    }

    public static void setUserName(String userName) {
        USER_NAME_HOLDER.set(userName);
    }

    public static String getUserName() {
        return USER_NAME_HOLDER.get();
    }

    public static void clear() {
        USER_ID_HOLDER.remove();
        USER_NAME_HOLDER.remove();
    }
}
