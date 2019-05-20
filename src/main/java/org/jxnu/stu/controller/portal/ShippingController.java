package org.jxnu.stu.controller.portal;

import com.github.pagehelper.PageInfo;
import org.jxnu.stu.common.*;
import org.jxnu.stu.controller.vo.ShippingVo;
import org.jxnu.stu.controller.vo.UserVo;
import org.jxnu.stu.dao.pojo.Shipping;
import org.jxnu.stu.service.ShippingService;
import org.jxnu.stu.util.CookieHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/shipping")
public class ShippingController {

    @Autowired
    private ShippingService shippingService;

    @Autowired
    private ValidationImpl validation;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "/add",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Map> add(Shipping shipping, HttpServletRequest request) throws BusinessException {
        String loggingToken = CookieHelper.readLoggingToken(request);
        if(loggingToken == null){
            throw new BusinessException(ReturnCode.USER_NOT_LOGIN);
        }
        UserVo userVo = (UserVo) redisTemplate.opsForValue().get(loggingToken);
        if(userVo == null){
            throw new BusinessException(ReturnCode.USER_NOT_LOGIN);
        }
        ValidationResult validationResult = validation.validate(shipping);
        if(validationResult.isHasError()){
            throw new BusinessException(ReturnCode.PARAMETER_VALUE_ERROR,validationResult.getErrMsg());
        }
        Map map = shippingService.add(shipping, userVo.getId());
        return ServerResponse.createServerResponse(ReturnCode.SUCCESS.getCode(),"新建地址成功",map);
    }

    @RequestMapping(value = "/del",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> del(Integer shippingId, HttpServletRequest request) throws BusinessException {
        String loggingToken = CookieHelper.readLoggingToken(request);
        if(loggingToken == null){
            throw new BusinessException(ReturnCode.USER_NOT_LOGIN);
        }
        UserVo userVo = (UserVo) redisTemplate.opsForValue().get(loggingToken);
        if(userVo == null){
            throw new BusinessException(ReturnCode.USER_NOT_LOGIN);
        }
        if(shippingId == null){
            throw new BusinessException(ReturnCode.PARAMETER_VALUE_ERROR);
        }
        shippingService.del(shippingId,userVo.getId());
        return ServerResponse.createServerResponse(ReturnCode.SUCCESS.getCode(),null,"删除地址成功");
    }

    @RequestMapping(value = "/update",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> update(Shipping shipping, HttpServletRequest request) throws BusinessException {
        String loggingToken = CookieHelper.readLoggingToken(request);
        if(loggingToken == null){
            throw new BusinessException(ReturnCode.USER_NOT_LOGIN);
        }
        UserVo userVo = (UserVo) redisTemplate.opsForValue().get(loggingToken);
        if(userVo == null){
            throw new BusinessException(ReturnCode.USER_NOT_LOGIN);
        }
        if(shipping.getId() == null){
            throw new BusinessException(ReturnCode.PARAMETER_VALUE_ERROR);
        }
        shippingService.update(shipping,userVo.getId());
        return ServerResponse.createServerResponse(ReturnCode.SUCCESS.getCode(),null,"更新地址成功");
    }

    @RequestMapping(value = "/select",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<ShippingVo> select(Integer shippingId, HttpServletRequest request) throws BusinessException {
        String loggingToken = CookieHelper.readLoggingToken(request);
        if(loggingToken == null){
            throw new BusinessException(ReturnCode.USER_NOT_LOGIN);
        }
        UserVo userVo = (UserVo) redisTemplate.opsForValue().get(loggingToken);
        if(userVo == null){
            throw new BusinessException(ReturnCode.USER_NOT_LOGIN);
        }
        if(shippingId == null){
            throw new BusinessException(ReturnCode.PARAMETER_VALUE_ERROR);
        }
        ShippingVo shippingVo = shippingService.select(shippingId, userVo.getId());
        return ServerResponse.createServerResponse(ReturnCode.SUCCESS.getCode(),shippingVo);
    }

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(defaultValue = "1") Integer pageNum,@RequestParam(defaultValue = "10") Integer pageSize,
                                           HttpServletRequest request) throws BusinessException {
        String loggingToken = CookieHelper.readLoggingToken(request);
        if(loggingToken == null){
            throw new BusinessException(ReturnCode.USER_NOT_LOGIN);
        }
        UserVo userVo = (UserVo) redisTemplate.opsForValue().get(loggingToken);
        if(userVo == null){
            throw new BusinessException(ReturnCode.USER_NOT_LOGIN);
        }
        PageInfo pageInfo = shippingService.list(pageNum, pageSize, userVo.getId());
        return ServerResponse.createServerResponse(ReturnCode.SUCCESS.getCode(),pageInfo);
    }






}
