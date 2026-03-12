package com.github.dingdaoyi.auth;

import cn.dev33.satoken.stp.StpUtil;
import com.github.dingdaoyi.entity.User;

import java.util.Optional;

/**
 * 认证工具类 - 封装 Sa-Token 常用操作
 * @author dingyunwei
 */
public class AuthUtil {

    private static final String USER_SESSION_KEY = "user";

    private AuthUtil() {}

    /**
     * 获取当前登录用户ID
     * @return 用户ID，未登录返回 null
     */
    public static Integer getUserId() {
        Object loginId = StpUtil.getLoginIdDefaultNull();
        return loginId != null ? Integer.parseInt(loginId.toString()) : null;
    }

    /**
     * 获取当前登录用户ID（必须登录）
     * @return 用户ID
     * @throws cn.dev33.satoken.exception.NotLoginException 未登录时抛出异常
     */
    public static Integer getRequiredUserId() {
        return Integer.parseInt(StpUtil.getLoginIdAsString());
    }

    /**
     * 获取当前登录用户名
     * @return 用户名，未登录返回 null
     */
    public static String getUsername() {
        return getUser().map(User::getUsername).orElse(null);
    }

    /**
     * 获取当前登录用户名（必须登录）
     * @return 用户名
     */
    public static String getRequiredUsername() {
        return getRequiredUser().getUsername();
    }

    /**
     * 获取当前登录用户对象
     * @return User 对象，未登录返回 Optional.empty()
     */
    public static Optional<User> getUser() {
        Object loginId = StpUtil.getLoginIdDefaultNull();
        if (loginId == null) {
            return Optional.empty();
        }
        try {
            User user = (User) StpUtil.getSession().get(USER_SESSION_KEY);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * 获取当前登录用户对象（必须登录）
     * @return User 对象
     * @throws cn.dev33.satoken.exception.NotLoginException 未登录时抛出异常
     */
    public static User getRequiredUser() {
        return (User) StpUtil.getSession().get(USER_SESSION_KEY);
    }

    /**
     * 刷新 Session 中的用户信息
     * @param user 更新后的用户对象
     */
    public static void refreshUser(User user) {
        if (user != null && user.getId() != null) {
            StpUtil.getSession().set(USER_SESSION_KEY, user);
        }
    }

    /**
     * 判断当前是否已登录
     * @return true 已登录
     */
    public static boolean isLogin() {
        return StpUtil.isLogin();
    }

    /**
     * 判断当前用户是否具有指定角色
     * @param role 角色标识
     * @return true 具有该角色
     */
    public static boolean hasRole(String role) {
        return StpUtil.hasRole(role);
    }

    /**
     * 判断当前用户是否具有指定权限
     * @param permission 权限标识
     * @return true 具有该权限
     */
    public static boolean hasPermission(String permission) {
        return StpUtil.hasPermission(permission);
    }

    /**
     * 退出登录
     */
    public static void logout() {
        StpUtil.logout();
    }

    /**
     * 获取 Token 信息
     * @return Token 值
     */
    public static String getTokenValue() {
        return StpUtil.getTokenValue();
    }
}
