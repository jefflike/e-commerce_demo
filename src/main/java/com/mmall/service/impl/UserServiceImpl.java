package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

// 向上注入的接口的属性名iUserService
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * @describe: 我们这里返回值就应该是高复用响应对象，这样在控制器就避免写业务逻辑，直接判断获得结果，这里应该是业务逻辑的主要编写
     * @param username
     * @param password
     * @return
     */
    @Override
    public ServerResponse<User> login(String username, String password) {

        // 检查登陆的用户名是否存在
        int resultCount = userMapper.checkUsername(username);

        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        // 预留一个密码登录的md5方法
        String md5Password = MD5Util.MD5EncodeUtf8(password);

        User user = userMapper.getUserByUserameAndPassword(username, md5Password);
        if(user == null){
            return ServerResponse.createByErrorMessage("密码有误");
        }

        // 我们不会将密码也返回到数据中，所以我们将它置空
        // 返回json中有字段password": "",后期优化去掉，可使用vo去掉这个字段
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功", user);
    }

    @Override
    public ServerResponse<String> regist(User user) {
        // 使用下面的方法将代码复用
        ServerResponse<String> stringServerResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        // 未验证成功的情况提前返回错误的高复用响应对象，结束方法
        if(!stringServerResponse.isSuccess()){
            return stringServerResponse;
        }
        stringServerResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if(!stringServerResponse.isSuccess()){
            return stringServerResponse;
        }
        // 设置当前用户的用户权限
        user.setRole(Const.Role.ROLE_CUSTOM);

        // 对密码进行md5加密，需要导入一个util工具
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        // checkUsername与checkEmail与此处可以只是用一个变量即可，都是一次性的，没必要浪费变量名
        int checkUsername = userMapper.insert(user);
        if(checkUsername == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    /**
     * isBlank只要是空就不行，空格返回也是false，isEmpty默认空格是非空的返回true
     * @param str: 前端input传回来的值
     * @param type: 前端input的类型，是判断email的还是username的实时判断
     * @return
     */
    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        // 返回的type必须有值才能进行判断
        if(org.apache.commons.lang3.StringUtils.isNotBlank(type)){
            if(Const.EMAIL.equals(type)){
                int checkEmail = userMapper.checkEmail(str);
                if(checkEmail > 0){
                    return ServerResponse.createByErrorMessage("邮箱已被注册");
                }
            }
            if(Const.USERNAME.equals(type)){
                int checkUsername = userMapper.checkUsername(str);
                if(checkUsername > 0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
        }else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    @Override
    public ServerResponse<String> getQuestionByUsername(String username) {
        // 还是要先判断用户是否存在
        ServerResponse<String> response = this.checkValid(username, Const.USERNAME);
        // 返回值要是success，那它的code就要是成功，checkValid当用户名不存在时可以插入，所以此时用户名不存在
        if(response.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.getQuestionByUsername(username);
        // 需要判断取到的问题不是空的
        if(org.apache.commons.lang3.StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccessMessage(question);
        }
        return ServerResponse.createByErrorMessage("设置的问题为空");
    }

    @Override
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        int account = userMapper.getAccountByUserAndQuesAndAnswer(username, question, answer);
        if(account > 0){
            // 确定提交的问题是正确的，需要添加token确保时效性，使用UUID生成唯一token
            String forgetToken = UUID.randomUUID().toString();
            // 将forgetToken放置本地cache中，并设置有效期，我们这里创建一个cache类
            TokenCache.setKey(TokenCache.TOKEN_PRIFIX + username, forgetToken);
            return ServerResponse.createBySuccessMessage(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String password, String forgetToken) {
        if(org.apache.commons.lang3.StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("token必须传递");
        }

        if(StringUtils.isBlank(password)){
            return ServerResponse.createByErrorMessage("密码参数异常");
        }
        // 接下来要检验username不能为空，如果为空那么cache中的key就变成了"Token_" + username，修改的就是别的null用户的密码了
        ServerResponse<String> response = this.checkValid(username, Const.USERNAME);
        if(response.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        // 获取这个用户在guava中存储的数据即Token
        String token = TokenCache.getKey(TokenCache.TOKEN_PRIFIX + username);

        // 判断取出的token为不为空
        if(org.apache.commons.lang3.StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或过期");
        }

        if(org.apache.commons.lang3.StringUtils.equals(token, forgetToken)){
            // 此时各种校验均通过，修改密码
            String md5Password = MD5Util.MD5EncodeUtf8(password);
            int rowCount = userMapper.updatePasswordByUsername(username, md5Password);

            // 生效行数判断
            if(rowCount > 0 ){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else {
            return ServerResponse.createByErrorMessage("token错误，请重新获取重置密码token");
        }
        return ServerResponse.createByErrorMessage("密码修改失败");
    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        // 登录状态下的密码修改需要注意防止横向越权行为，第一步要查询当前old密码与username是否在数据库中可以查询到，查不到就有问题了
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        // 这个更新方式是generator自动生成的，扩展性强，传入一个对象，更新此对象包含的字段
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0 ){
            return ServerResponse.createBySuccessMessage("更新密码成功");
        }else {
            return ServerResponse.createByErrorMessage("更新密码失败");
        }
    }

    /**
     * 登录状态更新用户的信息
     * @param user 更新用户的时候不可以更新用户的username。还需要校验email,email在数据库中要能查到，
     *             但不是我们当前的这个用户的email（当前就是这个email还换什么呢），就说明已经被别人占用了
     * @return
     */
    public ServerResponse<User> updateInformation(User user){
        // username不可以被更新
        int countEmail = userMapper.checkEmailByUserid(user.getEmail(), user.getId());
        // 大于0这说明这个email已经被其他用户占用了。
        if(countEmail > 0 ){
            return ServerResponse.createByErrorMessage("这个email已经被占用了，请尝试使用其他的email");
        }
        // 给更新的user对象赋以属性,属性都存在我么你的传进来user里面
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        // 使用这个方法而不是另一个，是因为这是一个动态sql另一个不论是不是null字段都会给你更新
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0 ){
            return ServerResponse.createBySuccess("更新用户信息成功", updateUser);
        }
        return ServerResponse.createByErrorMessage("更新用户信息失败");
    }

    public ServerResponse<User> selectInformation(Integer userid){
        User user = userMapper.selectByPrimaryKey(userid);
        if(user == null){
            ServerResponse.createByErrorMessage("未找到当前用户");
        }
        // 如果能够查询得到，我们将他的密码置空
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    // backend

    /**
     * 判断当前用户是不是管理员
     * @param user
     * @return
     */
    public ServerResponse checkRoleManager(User user){
        // 传入的用户不为空且用户是管理员身份
        if(user !=  null && user.getRole().intValue() == Const.Role.ROLE_MANAGER){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
