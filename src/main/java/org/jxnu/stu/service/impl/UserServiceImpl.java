package org.jxnu.stu.service.impl;

import com.alibaba.druid.util.StringUtils;
import org.jxnu.stu.common.*;
import org.jxnu.stu.controller.vo.UserVo;
import org.jxnu.stu.dao.UserMapper;
import org.jxnu.stu.dao.pojo.User;
import org.jxnu.stu.service.UserService;
import org.jxnu.stu.service.bo.UserBo;
import org.jxnu.stu.util.Md5Helper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserBo login(String username, String password) throws Exception{
        User user = userMapper.selectByUsername(username);
        if(user == null ){
            throw new BusinessException(ReturnCode.USER_NOT_EXIST);
        }
        String loginCrypyPassword = Md5Helper.encode(password);
        if(!StringUtils.equals(loginCrypyPassword,user.getPassword())){
            throw new BusinessException(ReturnCode.USER_LOGIN_FAILED);
        }
        return coverUserBoFromUserDo(user);
    }

    @Override
    public void register(User user) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        int j = userMapper.checkUserByUsername(user.getUsername());
        if(j > 0){
            throw new BusinessException(ReturnCode.USER_ALREADY_EXIST);
        }
        user.setRole(Constant.USER_ORDINARY);
        user.setPassword(Md5Helper.encode(user.getPassword()));
        int i = userMapper.insert(user);
        if(i <= 0){
            throw new BusinessException(ReturnCode.USER_REGISTE_FAILD);
        }
    }

    @Override
    public void checkValid(String str, String type) throws BusinessException {
        int count = 0;
        if(type.equals("email")){
            count = userMapper.checkUserByEmail(str);
        }
        if(type.equals("username")){
            count = userMapper.checkUserByUsername(str);
        }
        if(count > 0) {
            throw new BusinessException(ReturnCode.USER_ALREADY_EXIST);
        }
    }

    @Override
    public String forgetGetQuestion(String username) throws BusinessException {
        String question = userMapper.getQuestionByUsername(username);
        if(org.apache.commons.lang3.StringUtils.isBlank(question)){
            throw new BusinessException(ReturnCode.USER_NOT_HAS_QUESTION);
        }
        return question;
    }

    @Override
    public String forgetCheckAnswer(String username, String question, String answer, HttpSession session) throws BusinessException {
        int j = userMapper.checkUserByUsername(username);
        if(j <= 0){
            throw new BusinessException(ReturnCode.USER_NOT_EXIST);
        }
        int i = userMapper.forgetCheckAnswer(username, question, answer);
        if(i > 0){
            String token = UUID.randomUUID().toString();
            TokenCache.set(username,token);
            session.setAttribute(Constant.USER_FORGET_TOKEN,token);
            return token;
        }else{
            throw new BusinessException(ReturnCode.USER_ANSWER_WRONG);
        }
    }

    @Override
    public void forgetResetPassword(String username, String newPassword, String forgetToken) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        String cacheForgetToken = TokenCache.get(username);
        if(cacheForgetToken == null){
            throw new BusinessException(ReturnCode.ERROR,"请填写您重置密码时正确的用户名");
        }
        if(!com.alibaba.druid.util.StringUtils.equals(cacheForgetToken,forgetToken)){
            throw new BusinessException(ReturnCode.USER_FORGETTOKEN_ERROR,"请重新回答密保问题");
        }
        int i = userMapper.forgetResetPassword(username, Md5Helper.encode(newPassword));
        if(i <= 0){
            throw new BusinessException(ReturnCode.USER_RESET_PASSWORD_ERROR);
        }
    }

    @Override
    public void resetPassword(String passwordOld, String passwordNew, HttpSession session) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        UserVo userVo = (UserVo) session.getAttribute(Constant.CURRENT_USER);
        if(userVo == null){
            throw new BusinessException(ReturnCode.USER_NOT_LOGIN);
        }
        int i = userMapper.checkPasswordOld(userVo.getUsername(), Md5Helper.encode(passwordOld));
        if(i <= 0){
            throw new BusinessException(ReturnCode.PARAMETER_VALUE_ERROR,"用户原密码错误");
        }
        int j = userMapper.resetPassword(userVo.getUsername(), Md5Helper.encode(passwordNew));
        if(j <= 0){
            throw new BusinessException(ReturnCode.USER_RESET_PASSWORD_ERROR);
        }
    }

    @Override
    public void updateInformation(User user, HttpSession session) throws BusinessException {
        UserVo userVo = (UserVo) session.getAttribute(Constant.CURRENT_USER);
        if(userVo == null){
            throw new BusinessException(ReturnCode.USER_NOT_LOGIN);
        }
        user.setId(userVo.getId());
        int i = userMapper.updateByPrimaryKeySelective(user);
        if(i <= 0){
            throw new BusinessException(ReturnCode.USER_INFO_UPDATE_ERROR);
        }
    }

    public UserBo coverUserBoFromUserDo(User user){
        if(user == null){
            return null;
        }
        UserBo userBo = new UserBo();
        BeanUtils.copyProperties(user,userBo);
        return userBo;
    }
}
