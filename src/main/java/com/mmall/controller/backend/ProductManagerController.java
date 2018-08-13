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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
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

    /**
     * 搜索数据页面，包括通过名称，id搜索，对搜索结果还需要分页操作
     * @param session
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse productSearch(HttpSession session,String productName,Integer productId, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NO_LOGIN.getCode(),"用户未登录,请登录管理员");

        }
        if(iUserService.checkRoleManager(user).isSuccess()){
            //填充业务
            return iProductService.searchProduct(productName,productId,pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("当前用户无管理员权限");
        }
    }

    /**
     * 图片的上传控制器
     * @param session
     * @param file
     * @param request
     * @return
     */
    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session, @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NO_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        if(iUserService.checkRoleManager(user).isSuccess()){
            // 填充业务，上传图片

        }else{
            return ServerResponse.createByErrorMessage("当前用户无管理员权限");
        }
    }
}
