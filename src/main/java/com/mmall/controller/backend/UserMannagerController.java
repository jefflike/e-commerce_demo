package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 管理员登录的控制器
 * 1. 注解被springmvc管理
 * 2. 这是一个管理用户模块的控制器
 */
@Controller
@RequestMapping("/manager/user")
public class UserMannagerController {
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse<User> response = iUserService.login(username, password);
        if(response.isSuccess()){
            User user = response.getData();
            // 当登录的用户的权限是一个管理员的时候，我们才将他放到session域中
            if(user.getRole() == Const.Role.ROLE_MANAGER ){
                session.setAttribute(Const.CURRENT_USER, user);
                // 成功才将response返回去
                return response;
            }
            return ServerResponse.createByErrorMessage("当前用户不是管理员用户");
        }
         // 不是管理员那就是错误，我们直接返回这个response就好了（我们在login进行了判断，登录失败有登录失败的response）
        return response;
    }
}
