package com.mmall.common;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

/**
 * 这是一个通用端响应对象，T代表响应封装的数据对象是什么类型
 * JsonSerialize（有的没有msg有的没有data）将没有的就取消掉，返回的时候就不包含
 * 这个注解的Inclusion.NON_NULL就是序列化的时候，如果是null的对象，key也会消失
 * @param <T>
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable {
    private int status;
    private String  msg;
    private T data;

    private ServerResponse(int status){
        this.status = status;
    }

    private ServerResponse(int status, String msg){
        this.status = status;
        this.msg = msg;
    }

    private ServerResponse(int status, String msg, T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    private ServerResponse(int status, T data){
        this.status = status;
        this.data = data;
    }

    // JsonIgnore学历恶化默认忽略这个字段
    @JsonIgnore
    public boolean isSuccess(){
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    public int getStatus(){
        return status;
    }

    public String getMsg(){
        return msg;
    }

    public T getData(){
        return data;
    }

    // 需要指定泛型，返回前端是msg或者data是不确定的所以需要泛型指定
    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> createBySuccessMessage(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg);
    }

    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), data);
    }

    public static <T> ServerResponse<T> createBySuccess(String msg,T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg, data);
    }

    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getDesc());
    }

    // 创建错误返回一个提示，参数传递一个错误信息
    public static <T> ServerResponse<T> createByErrorMessage(String errorMessage){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(), errorMessage);
    }

    // 除了上述两种状态，还有未登录，参数错误等异常需要操作
    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode, String errorMessage){
        return new ServerResponse<T>(errorCode, errorMessage);
    }

    /**
     *   测试，当第二个构造器调用不同的参数的时候，是调用string构造器还是T构造器，当我们返回data就是string的时候，我们就要
     *   在获取的public方法中做一些操作了，不同的获取方式返回的类型就不同，createBySuccessMessage与
     *   createBySuccess
     *   public static void main(String[] args) {
     *      ServerResponse sr1 = new ServerResponse(1, "1");
     *       ServerResponse sr2 = new ServerResponse(1, new Object());
    }*/
}
