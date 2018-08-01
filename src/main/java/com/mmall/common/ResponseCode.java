package com.mmall.common;

/**
 * 获取状态码的一些信息，枚举的属性间使用，不能使用；
 * 需要定义常亮作为私有构造器的属性
 * 枚举类型需要定义public的get获取相应的参数信息
 * 更多错误的状态，可以通过增加枚举的类型来实现
 */
public enum ResponseCode {
    SUCCESS(0, "SUCCESS"),
    ERROR(1, "ERROR"),
    NO_LOGIN(10, "NO_LOGIN"),
    ILLEGAL_ARGUMENT(2, "ILLEGAL_ARGUMENT");

    private final int code;
    private final String desc;

    private ResponseCode(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public ResponseCode getResponseCode(){
        return null;
    }
}
