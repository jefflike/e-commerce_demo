package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/product")
public class ProductManagerController {

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IUserService iUserService;

    /**
     * 添加商品
     * @param session
     * @param product
     * @return
     */
    @RequestMapping("save.do")
    @ResponseBody
    ServerResponse productSave(HttpSession session, Product product){
        User user = (User) session.getAttribute(Const.USERNAME);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NO_LOGIN.getCode(), "请登录再操作");
        }
        ServerResponse serverResponse = iUserService.checkRoleManager(user);
        if(serverResponse.isSuccess()){
            // 增加产品的业务逻辑
            return iProductService.saveOrUpdateProduct(product);
        }else {
            return ServerResponse.createByErrorMessage("当前用户无管理员权限");
        }
    }

    @RequestMapping("setSaleStatus.do")
    @ResponseBody
    ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status){
        User user = (User) session.getAttribute(Const.USERNAME);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NO_LOGIN.getCode(), "请登录再操作");
        }
        ServerResponse serverResponse = iUserService.checkRoleManager(user);
        if(serverResponse.isSuccess()){
//            return iProductService.saveOrUpdateProduct();
        }else {
            return ServerResponse.createByErrorMessage("当前用户无管理员权限");
        }
    }

    // 获取产品详情
    @RequestMapping("getProductMessage.do")
    @ResponseBody
    ServerResponse getProductMessage(HttpSession session, Integer productId, Integer status){
        User user = (User) session.getAttribute(Const.USERNAME);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NO_LOGIN.getCode(), "请登录再操作");
        }
        ServerResponse serverResponse = iUserService.checkRoleManager(user);
        if(serverResponse.isSuccess()){
            return null;
        }else {
            return ServerResponse.createByErrorMessage("当前用户无管理员权限");
        }
    }
}
