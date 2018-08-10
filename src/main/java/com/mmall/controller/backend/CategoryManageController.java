package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/**
 * 分类管理模块
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {
    @Autowired
    private IUserService iUserService;

    /**
     *
     * @param session 校验登陆的用户是不是管理员
     * @param parentId parentId需要做一个限制，当我们未传递parentId的时候希望他有一个默认的值0,也就是根目录
     * @return
     */
    public ServerResponse addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") int parentId){
        // 第一步先判断用户是否登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if( user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NO_LOGIN.getCode(), "请先登录账户");
        }
        // 第二部判断用户是否是管理员
        if(iUserService.checkRoleManager(user).isSuccess()){

        }
        return ServerResponse.createByErrorMessage("权限不足，需要管理员权限");
    }
}
