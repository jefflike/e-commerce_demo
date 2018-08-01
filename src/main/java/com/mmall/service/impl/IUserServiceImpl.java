package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// 向上注入的接口的属性名iUserService
@Service("iUserService")
public class IUserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {

        // 检查登陆的用户名是否存在
        int resultCount = userMapper.checkUsername(username);

        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("用户名或密码有误");
        }

        // 预留一个密码登录的md5方法
        String md5Password = MD5Util.MD5EncodeUtf8(password);

        User user = userMapper.getUserByUserameAndPassword(username, md5Password);
        if(user == null){
            return ServerResponse.createByErrorMessage("密码有误");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功", user);
    }

    @Override
    public ServerResponse<String> regist(User user) {
        // 使用下面的方法将代码复用
        ServerResponse<String> stringServerResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if(!stringServerResponse.isSuccess()){
            return stringServerResponse;
        }
        stringServerResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if(!stringServerResponse.isSuccess()){
            return stringServerResponse;
        }
        // 设置当前用户的用户权限
        user.setRole(Const.Role.ROLE_CUSTOM);

        // 对密码进行md5加密，需要导入一个util工具
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        // checkUsername与checkEmail与此处可以只是用一个变量即可，都是一次性的，没必要浪费变量名
        int checkUsername = userMapper.insert(user);
        if(checkUsername == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createByErrorMessage("注册成功");
    }

    /**
     * isBlank只要是空就不行，空格返回也是false，isEmpty默认空格是非空的返回true
     * @param str
     * @param type
     * @return
     */
    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if(org.apache.commons.lang3.StringUtils.isBlank(type)){
            if(Const.EMAIL.equals(type)){
                int checkEmail = userMapper.checkEmail(str);
                if(checkEmail > 0){
                    return ServerResponse.createByErrorMessage("邮箱已被注册");
                }
            }
            if(Const.USERNAME.equals(type)){
                int checkUsername = userMapper.checkUsername(str);
                if(checkUsername > 0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
        }else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }
}
