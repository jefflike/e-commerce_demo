package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
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
import org.springframework.web.bind.annotation.RequestParam;
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
    public ServerResponse productSave(HttpSession session, Product product){
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

    /**
     * 改变商品的状态
     * @param session
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping("setSaleStatus.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status){
        User user = (User) session.getAttribute(Const.USERNAME);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NO_LOGIN.getCode(), "请登录再操作");
        }
        ServerResponse serverResponse = iUserService.checkRoleManager(user);
        if(serverResponse.isSuccess()){
//            return iProductService.saveOrUpdateProduct();
            return iProductService.setSaleStatus(productId, status);
        }else {
            return ServerResponse.createByErrorMessage("当前用户无管理员权限");
        }
    }

    /** 获取产品详情
     * @param session
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping("getProductDetail.do")
    @ResponseBody
    public ServerResponse getProductDetail(HttpSession session, Integer productId, Integer status){
        User user = (User) session.getAttribute(Const.USERNAME);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NO_LOGIN.getCode(), "请登录再操作");
        }
        ServerResponse serverResponse = iUserService.checkRoleManager(user);
        if(serverResponse.isSuccess()){
            return iProductService.manageProductDetail(productId);
        }else {
            return ServerResponse.createByErrorMessage("当前用户无管理员权限");
        }
    }

    /**
     * 管理后台产品list，数据较多，需要做分页
     * @param session
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping("getProductList.do")
    @ResponseBody
    public ServerResponse getProductList(HttpSession session, @RequestParam(value = "pageno", defaultValue = "1") int pageno, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        User user = (User) session.getAttribute(Const.USERNAME);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NO_LOGIN.getCode(), "请登录再操作");
        }
        ServerResponse serverResponse = iUserService.checkRoleManager(user);
        if(serverResponse.isSuccess()){
            //需要动态分页,使用插件实现非常简单，三步走
            return iProductService.getProductList(pageno, pageSize);
        }else {
            return ServerResponse.createByErrorMessage("当前用户无管理员权限");
        }
    }
}
