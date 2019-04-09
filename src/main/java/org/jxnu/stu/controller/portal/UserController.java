package org.jxnu.stu.controller.portal;

import org.apache.commons.lang3.StringUtils;
import org.jxnu.stu.common.*;
import org.jxnu.stu.controller.vo.UserVo;
import org.jxnu.stu.dao.UserMapper;
import org.jxnu.stu.dao.pojo.User;
import org.jxnu.stu.service.UserService;
import org.jxnu.stu.service.bo.UserBo;
import org.jxnu.stu.util.DateTimeHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ValidationImpl validator;

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<UserVo> login(String username, String password, HttpSession session) throws Exception {
        if(StringUtils.isBlank(username)){
            throw new BusinessException(ReturnCode.PARAMETER_VALUE_ERROR,"用户名不能为空");
        }
        if(StringUtils.isBlank(password)){
            throw new BusinessException(ReturnCode.PARAMETER_VALUE_ERROR,"密码不能为空");
        }
        UserBo userBo = userService.login(username, password);
        UserVo userVo = coverUserVoFromUserBo(userBo);
        session.setAttribute(Constant.CURRENT_USER,userVo);
        return ServerResponse.createServerResponse(ReturnCode.SUCCESS.getCode(),ReturnCode.USER_LOGIN_SUCCESS.getMsg(),userVo);
    }

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(@Valid User user) throws BusinessException, NoSuchAlgorithmException, UnsupportedEncodingException {
        ValidationResult validationResult = validator.validate(user);
        if(validationResult.isHasError()){
            throw new BusinessException(ReturnCode.PARAMETER_VALUE_ERROR,validationResult.getErrMsg());
        }
        userService.register(user);
        return ServerResponse.createServerResponse(ReturnCode.SUCCESS.getCode(),"注册成功！");
    }

    @RequestMapping(value = "/check_valid",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type) throws BusinessException {
        if(StringUtils.isBlank(str)){
            throw new BusinessException(ReturnCode.PARAMETER_VALUE_ERROR,"用户名不能为空");
        }
        if(StringUtils.isBlank(type)){
            throw new BusinessException(ReturnCode.PARAMETER_VALUE_ERROR,"用户名类型不能为空");
        }
        userService.checkValid(str,type);
        return ServerResponse.createServerResponse(ReturnCode.SUCCESS.getCode());
    }

    @RequestMapping(value = "/get_user_info",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<UserVo> getUserInfo(HttpSession session) throws Exception {
        UserVo userVo = (UserVo) session.getAttribute(Constant.CURRENT_USER);
        if(userVo == null){
            throw new BusinessException(ReturnCode.USER_NOT_LOGIN,"用户没有登陆无法获取当前用户信息");
        }
        return ServerResponse.createServerResponse(ReturnCode.SUCCESS.getCode(),userVo);
    }

    @RequestMapping(value = "/forget_get_question",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username) throws Exception {
        if(StringUtils.isBlank(username)){
            throw new BusinessException(ReturnCode.PARAMETER_VALUE_ERROR,"请输入用户名");
        }
        String question = userService.forgetGetQuestion(username);
        return ServerResponse.createServerResponse(ReturnCode.SUCCESS.getCode(),null,question);
    }

    @RequestMapping(value = "/forget_check_answer",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer,HttpSession session) throws Exception{
        if(StringUtils.isBlank(username)){
            throw new BusinessException(ReturnCode.PARAMETER_VALUE_ERROR,"用户名不能为空");
        }
        if(StringUtils.isBlank(question)){
            throw new BusinessException(ReturnCode.PARAMETER_VALUE_ERROR,"密保问题不能为空");
        }
        if(StringUtils.isBlank(answer)){
            throw new BusinessException(ReturnCode.PARAMETER_VALUE_ERROR,"密保回答不能为空");
        }
        String token = userService.forgetCheckAnswer(username, question, answer, session);
        return ServerResponse.createServerResponse(ReturnCode.SUCCESS.getCode(),null,token);
    }

    @RequestMapping(value = "/forget_reset_password",method = RequestMethod.POST)
    @ResponseBody
        public ServerResponse<String> forgetResetPassword(String username,String newPassword,String forgetToken) throws Exception {
        if(StringUtils.isBlank(username)){
            throw new BusinessException(ReturnCode.PARAMETER_VALUE_ERROR,"用户名不能为空!");
        }
        if(StringUtils.isBlank(newPassword)){
            throw new BusinessException(ReturnCode.PARAMETER_VALUE_ERROR,"密保问题不能为空");
        }
        if(StringUtils.isBlank(forgetToken)){
            throw new BusinessException(ReturnCode.PARAMETER_VALUE_ERROR,"forgetToken为空");
        }
        userService.forgetResetPassword(username,newPassword,forgetToken);
        return ServerResponse.createServerResponse(ReturnCode.SUCCESS.getCode(),"修改密码成功");
    }

    @RequestMapping(value = "/reset_password",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(String passwordOld,String passwordNew,HttpSession session) throws Exception {
        if(StringUtils.isBlank(passwordOld)){
            throw new BusinessException(ReturnCode.PARAMETER_VALUE_ERROR,"旧密码不能为空");
        }
        if(StringUtils.isBlank(passwordOld)){
            throw new BusinessException(ReturnCode.PARAMETER_VALUE_ERROR,"新密码不能为空");
        }
        userService.resetPassword(passwordOld,passwordNew,session);
        return ServerResponse.createServerResponse(ReturnCode.SUCCESS.getCode(),"修改密码成功");
    }

    @RequestMapping(value = "/update_information",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> updateInformation(User user,HttpSession session) throws Exception {
        userService.updateInformation(user,session);
        return ServerResponse.createServerResponse(ReturnCode.SUCCESS.getCode(),"更新个人信息成功");
    }

    @RequestMapping(value = "/getInformation",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<UserVo> getInformation(HttpSession session) throws Exception{
        UserVo userVo = (UserVo) session.getAttribute(Constant.CURRENT_USER);
        if (userVo == null){
            throw new BusinessException(ReturnCode.USER_NOT_LOGIN);
        }
        return ServerResponse.createServerResponse(ReturnCode.SUCCESS.getCode(),userVo);
    }

    @RequestMapping(value = "/logout",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session) throws BusinessException {
        if(session.getAttribute(Constant.CURRENT_USER) == null){
            throw new BusinessException(ReturnCode.USER_NOT_LOGIN);
        }
        session.removeAttribute(Constant.CURRENT_USER);
        if(session.getAttribute(Constant.CURRENT_USER) == null){
            return ServerResponse.createServerResponse(ReturnCode.SUCCESS.getCode(),"退出成功");
        }
        return ServerResponse.createServerResponse(ReturnCode.ERROR.getCode(),"服务器异常");
    }

    public UserVo coverUserVoFromUserBo(UserBo userBo) throws BusinessException {
        if(userBo == null){
            return null;
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(userBo,userVo);
        userVo.setCreateTime(DateTimeHelper.dateToString(userBo.getCreateTime()));
        userVo.setUpdateTime(DateTimeHelper.dateToString(userBo.getUpdateTime()));
        return userVo;
    }

}
