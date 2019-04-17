package org.jxnu.stu.service;

import org.jxnu.stu.common.BusinessException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface OrderService {

    Map pay(String orderNo, Integer userId, String path) throws Exception;

    Boolean queryOrderPayStatus(String orderNo,Integer userId) throws BusinessException;

    String alipayCallback(HttpServletRequest request) throws BusinessException;

}
