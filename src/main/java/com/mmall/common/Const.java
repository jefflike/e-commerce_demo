package com.mmall.common;

/**
 * 声明项目所需要的常量
 */
public class Const {
    public static final String  CURRENT_USER = "current_user";

    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    // 内部的一个接口类，模拟一个类似枚举的功能，即分组功能（简单认为是轻量级枚举）
    public static interface Role{
        int ROLE_CUSTOM = 0;
        int ROLE_MANAGER = 1;
    }
}
