package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.service.impl.IUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 用户模块
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录模块,除了方便测试，所有的用户相关的接口都要是post方法
     * ResponseBody返回的时候自动调用springmvc的jackson插件将返回值json序列化
     * 在dispatch-servlet.xml中配置supportedMediaTypes属性为json我们返回值默认就被赋值为json类型了
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse<User> response = iUserService.login(username, password);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     *  这里泛型的值应该是String
     * @param user
     * @return
     */
    @RequestMapping(value = "regist.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> regist(User user){
        ServerResponse<String> response = iUserService.regist(user);
        return response;
    }

    /**
     *  检验email和用户名是否存在，在注册时虽然有判断，此处进行判断是因为方便ajax实时的判断
     *  检验email与用户名存在的状态
     */
    @RequestMapping(value = "checkValid.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type){
        ServerResponse<String> serverResponse = iUserService.checkValid(str, type);
        return serverResponse;
    }
}
