package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 分类管理模块
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {
    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    /**
     *
     * @param session 校验登陆的用户是不是管理员
     * @param parentId parentId需要做一个限制，当我们未传递parentId的时候希望他有一个默认的值0,也就是根目录
     * @return
     */
    @RequestMapping("addCategory.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") int parentId){
        // 第一步先判断用户是否登录(可以抽取方法单独做成一个功能)
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if( user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NO_LOGIN.getCode(), "请先登录账户");
        }
        // 第二步判断用户是否是管理员
        if(iUserService.checkRoleManager(user).isSuccess()){
            // 有管理员权限，进行增加种类的逻辑
            return iCategoryService.addCategory(categoryName, parentId);
        }
        return ServerResponse.createByErrorMessage("权限不足，需要管理员权限");
    }

    /**
     * 修改当前类别的名称
     */
    @RequestMapping("setCategoryName.do")
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session, Integer categoryId, String categoryName){
        // 第一步先判断用户是否登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if( user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NO_LOGIN.getCode(), "请先登录账户");
        }
        // 第二部判断用户是否是管理员
        if(iUserService.checkRoleManager(user).isSuccess()){
            // 有管理员权限，进行修改种类名的逻辑
            return iCategoryService.updateCategoryName(categoryId, categoryName);
        }
        return ServerResponse.createByErrorMessage("权限不足，需要管理员权限");
    }

    /**
     * 获取子类平级的类型，如果categoryId没有赋值，那么我们就默认是找根节点
     */
    @RequestMapping("getChildrenParallelCategory.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpSession session, @RequestParam(value = "categoryId" ,defaultValue = "0") Integer categoryId){
        // 第一步先判断用户是否登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if( user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NO_LOGIN.getCode(), "请先登录账户");
        }
        // 第二部判断用户是否是管理员
        if(iUserService.checkRoleManager(user).isSuccess()){
            // 查询子节点的category信息,并且不递归,保持平级
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }
        return ServerResponse.createByErrorMessage("权限不足，需要管理员权限");
    }

    /**
     * 获取当前类并且递归查询它的子类的控制器
     */
    @RequestMapping("getCategoryAndDeepChildrenCategory.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session,@RequestParam(value = "categoryId" ,defaultValue = "0") Integer categoryId){
        // 第一步先判断用户是否登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if( user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NO_LOGIN.getCode(), "请先登录账户");
        }
        // 第二部判断用户是否是管理员
        if(iUserService.checkRoleManager(user).isSuccess()){
            // 查询当前节点的id并递归查询所有的子节点的categoryId
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        }
        return ServerResponse.createByErrorMessage("权限不足，需要管理员权限");
    }
}
