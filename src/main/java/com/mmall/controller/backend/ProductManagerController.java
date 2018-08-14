package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/manage/product")
public class ProductManagerController {

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IFileService iFileService;

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
     * @param file springmvc的文件上传
     * @param request 通过servlet的动态上下文获取到一个路径来
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
            // 第一步先获取动态的上下文路径
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;

            Map fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);
            return ServerResponse.createBySuccess(fileMap);
        }else{
            return ServerResponse.createByErrorMessage("当前用户无管理员权限");
        }
    }


    /**
     * 富文本框上传图片
     */
    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        Map resultMap = Maps.newHashMap();
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
        }
        //富文本中对于返回值有自己的要求,我们使用是simditor所以按照simditor的要求进行返回
//        {
//            "success": true/false,
//                "msg": "error message", # optional
//            "file_path": "[real file path]"
//        }
        if(iUserService.checkRoleManager(user).isSuccess()){
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            if(StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultMap;
        }else{
            resultMap.put("success",false);
            resultMap.put("msg","无权限操作");
            return resultMap;
        }
    }
}

