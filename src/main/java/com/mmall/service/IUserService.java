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
    ServerResponse<String> getQuestionByUsername(String userame);
    ServerResponse<String> forgetCheckAnswer(String username, String question, String answer);
    ServerResponse<String> forgetResetPassword(String username, String password, String forgetToken);
    ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user);
    ServerResponse<User> updateInformation(User user);
    ServerResponse<User> selectInformation(Integer userid);
    ServerResponse checkRoleManager(User user);
}
