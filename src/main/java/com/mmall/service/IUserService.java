package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * I开头方便维护时知道这是一个接口
 */
public interface IUserService {
    ServerResponse<User> login(String username, String password);
    ServerResponse<String> regist(User user);
    ServerResponse<String> checkValid(String str, String type);

}
