package org.jxnu.stu.service;

import com.github.pagehelper.PageInfo;
import org.jxnu.stu.common.BusinessException;
import org.jxnu.stu.controller.vo.ShippingVo;
import org.jxnu.stu.dao.pojo.Shipping;

import java.util.Map;

public interface ShippingService {

    Map add(Shipping shipping, Integer userId) throws BusinessException;

    void del(Integer shippingId, Integer userId) throws BusinessException;

    void update(Shipping shipping, Integer userId) throws BusinessException;

    ShippingVo select(Integer shippingId, Integer userId) throws BusinessException;

    PageInfo list(Integer pageNum, Integer pageSize, Integer userId) throws BusinessException;

}
