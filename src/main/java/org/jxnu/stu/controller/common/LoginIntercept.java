package org.jxnu.stu.controller.common;

import com.alibaba.druid.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.jxnu.stu.common.Constant;
import org.jxnu.stu.common.ReturnCode;
import org.jxnu.stu.common.ServerResponse;
import org.jxnu.stu.controller.vo.UserVo;
import org.jxnu.stu.util.CookieHelper;
import org.jxnu.stu.util.JsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

@Slf4j
@Component
public class LoginIntercept implements HandlerInterceptor {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("请求的url为:{}",request.getRequestURL());
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getSimpleName();
        Map<String, String[]> parameterMap = request.getParameterMap();
        Iterator<String> iterator = parameterMap.keySet().iterator();
        StringBuilder parameters = new StringBuilder();
        while (iterator.hasNext()){
            String key = iterator.next();
            String[] values = parameterMap.get(key);
            String value = Arrays.toString(values);
            parameters.append(key+"="+value+";");
        }
        if(StringUtils.equals("UserController",className) && StringUtils.equals("login",methodName)){
            log.info("权限拦截器拦截到请求，ClassName:{}，MethodName:{}",className,methodName);
            return true;
        }
        UserVo userVo = null;
        String loggingToken = CookieHelper.readLoggingToken(request);
        if(!StringUtils.isEmpty(loggingToken)){
            userVo = (UserVo) redisTemplate.opsForValue().get(loggingToken);
        }
        if(userVo == null || userVo.getRole().intValue() != Constant.USER_ORDINARY){
            //即不调用controller的方法，直接返回response给用户
            response.reset();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            if(userVo == null){
                writer.print(JsonHelper.obj2string(ServerResponse.createServerResponse(ReturnCode.USER_NOT_LOGIN.getCode(),ReturnCode.USER_NOT_LOGIN.getMsg())));
                //throw new BusinessException(ReturnCode.USER_NOT_LOGIN);
            }else if(userVo.getRole().intValue() != Constant.USER_ORDINARY){
                writer.print(JsonHelper.obj2string(ServerResponse.createServerResponse(ReturnCode.USER_HAS_NO_PERMISSION.getCode(),ReturnCode.USER_HAS_NO_PERMISSION.getMsg())));
                //throw new BusinessException(ReturnCode.ERROR,"用户无权限");
            }
            writer.flush();
            writer.close();
            return false;
        }

        log.info("request parameters is:{}",parameters.toString());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
