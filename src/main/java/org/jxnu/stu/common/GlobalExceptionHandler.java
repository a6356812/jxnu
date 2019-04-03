package org.jxnu.stu.common;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public <T> ServerResponse<T> exceptionHandler(HttpServletRequest request, HttpServletResponse response,Exception e){
        if(e instanceof  BusinessException){
            BusinessException exception = (BusinessException) e;
            return ServerResponse.createServerResponse(exception.getCode(),exception.getMsg());
        }else {
            return ServerResponse.createServerResponse(ReturnCode.ERROR.getCode(),ReturnCode.ERROR.getMsg());
        }
    }
}
