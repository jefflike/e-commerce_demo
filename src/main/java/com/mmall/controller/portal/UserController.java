package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
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
 * 用户模块,所有的用户接口包括管理员都使用post方式提交
 * 我们的控制器里避免写大量的业务代码，我这里是没有写业务的，控制器里非常清晰
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
     * 需要使用到HttpSession
     * Service里对于错误的复用响应对象已经实现了，所以Controller中可以不出现new 复用响应对象
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
            // 登陆成功将用户信息存到session域中,data此时就是user对象
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    /**
     * 虽然只是logout但是还是要返回高复用响应对象
     * @param session
     * @return
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
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
    @RequestMapping(value = "regist.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> regist(User user){
//        ServerResponse<String> response = iUserService.regist(user);
        return iUserService.regist(user);
    }

    /**
     *  检验email和用户名是否存在，在注册时虽然有判断，此处进行判断是因为方便ajax实时的判断
     *  检验email与用户名存在的状态
     */
    @RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type){
        // 简化代码，舍去不必要的参数
//        ServerResponse<String> serverResponse = iUserService.checkValid(str, type);
        return iUserService.checkValid(str, type);
    }

    /**
     * 获取用户信息， 我们前面不是将用户的信息都放到session域中了吗，所以这里只要session就可以了，不需要传入id
     * 既然已经在session域中了，我们就不要查询数据库了，没必要，直接处理就好了
     * 这里也有一点小问题，返回的json包括了问题答案的一些东西，后期在vo中过滤
     */
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user != null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息");
    }


    /**
     * 忘记密码的找回问题
     */
    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> getQuestionByUsername(String username){
        return iUserService.getQuestionByUsername(username);
    }

    /**
     * 判断找回密码问题的答案是否正确
     * 需要使用guava解决本地缓存问题，用本地guava做缓存，利用有效期管理
     */
    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer){
        return iUserService.forgetCheckAnswer(username, question, answer);
    }

    /**
     * 忘记密码的重置密码，需要问题、答案、有效Token和新密码md5加密后的值，Token的值从前端获取，并与guava中的token作对比
     */
    @RequestMapping(value = "forget_reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username, String password, String forgetToken){
        return iUserService.forgetResetPassword(username,password,forgetToken);
    }

    /**
     * 登录状态的重置密码
     */
    @RequestMapping(value = "resetPassword.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session, String passwordOld, String passwordNew){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage("请先登录或者忘记密码");
        }
        return iUserService.resetPassword(passwordOld, passwordNew, user);
    }
    /**
     * 更新个人用户信息的方法，我们的返回值是一个user，我们在更新完用户信息之后，要将新的用户信息放到session里，同时要把新的用户信息返回给前端
     * 然后前端把信息直接跟新到页面上,形参的user对象是我们用来承载新的数据的对象，user中都是常规信息，但是没有userid，防止横向越权行为,需要先将
     * 当前的用户id赋值给他
     */
    @RequestMapping(value = "updateInformation.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpSession session, User user){
        // 第一不还是先判断用户是否登录
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ServerResponse.createByErrorMessage("请先登录或者忘记密码");
        }
        // 我们的user封装的信息有很多，但是没有id的值，我们需要取出当前登录的user的id赋值进去
        // 这样做的原因是防止横向越权，我们把id就指定为当前用户的id，user就是当前session对象中的username
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = iUserService.updateInformation(user);
        // 如果更新是成功的，则更新我们的session到域中
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER, response);
        }
        return response;
    }

    /**
     * 获取用户的详细信息
     * 修改信息之前会先查询当前的用户信息，必须保证用户登录，update的时候（前一步肯定是先查，查的时候已经强制登录过了）只需要判断是否登录即可，不需要强制提示登录
     * 查提问问题或者提问答案都可以通过此接口来实现了
     */
    @RequestMapping(value = "selectInformation.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> selectInformation(HttpSession session){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        // 与前端约定，一旦我们给前端传10过去，前端就要强制进行登录（跳转登录页面）
        if(currentUser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NO_LOGIN.getCode(),"必须要先登录status=10");
        }
        return iUserService.selectInformation(currentUser.getId());
    }
}
