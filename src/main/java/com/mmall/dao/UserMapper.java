package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String userame);

    // 传递多个参数需要使用param注解
    User getUserByUserameAndPassword(@Param("username") String username, @Param("password") String password);

    int checkEmail(String email);

    String getQuestionByUsername(String username);

    int getAccountByUserAndQuesAndAnswer(@Param("username") String username, @Param("question") String question, @Param("answer") String answer);

    int updatePasswordByUsername(@Param("username") String username, @Param("password") String password);

    int checkPassword(@Param("password") String password, @Param("userId") Integer userId);

    int checkEmailByUserid(@Param("email") String email, @Param("userId") Integer userid);
}