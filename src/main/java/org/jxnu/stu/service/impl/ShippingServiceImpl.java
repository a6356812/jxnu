package org.jxnu.stu.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.jxnu.stu.common.BusinessException;
import org.jxnu.stu.common.ReturnCode;
import org.jxnu.stu.controller.vo.ShippingVo;
import org.jxnu.stu.dao.ShippingMapper;
import org.jxnu.stu.dao.pojo.Shipping;
import org.jxnu.stu.service.ShippingService;
import org.jxnu.stu.util.DateTimeHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShippingServiceImpl implements ShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public Map add(Shipping shipping, Integer userId) throws BusinessException {
        shipping.setUserId(userId);
        int i = shippingMapper.insert(shipping);
        if (i < 1) {
            throw new BusinessException(ReturnCode.SHIPPING_ADD_ERROR);
        }
        Map<String, Integer> map = new HashMap<>();
        map.put("shippingId", shipping.getId());
        return map;
    }

    @Override
    public void del(Integer shippingId, Integer userId) throws BusinessException {
        int i = shippingMapper.delByUserIdShippingId(shippingId, userId);
        if (i < 1) {
            throw new BusinessException(ReturnCode.SHIPPING_DEL_ERROR);
        }
    }

    @Override
    public void update(Shipping shipping, Integer userId) throws BusinessException {
        shipping.setUserId(userId);
        int i = shippingMapper.updateByUserIdShippingIdSelective(shipping);
        if (i < 1) {
            throw new BusinessException(ReturnCode.SHIPPING_UPDATE_ERROR);
        }
    }

    @Override
    public ShippingVo select(Integer shippingId, Integer userId) throws BusinessException {
        Shipping shipping = shippingMapper.selectByUserIdShippingId(shippingId, userId);
        if (shipping == null) {
            throw new BusinessException(ReturnCode.SHIPPING_NOT_EXIST);
        }
        return coverShippingVoFromShippingDo(shipping);
    }

    @Override
    public PageInfo list(Integer pageNum, Integer pageSize, Integer userId) throws BusinessException {
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippingList = shippingMapper.listAllByUserId(userId);
        List<ShippingVo> shippingVoList = new ArrayList<>();
        for (Shipping shipping : shippingList) {
            shippingVoList.add(coverShippingVoFromShippingDo(shipping));
        }
        PageInfo pageInfo = new PageInfo(shippingVoList);
        return pageInfo;
    }

    private ShippingVo coverShippingVoFromShippingDo(Shipping shipping) throws BusinessException {
        if (shipping == null) {
            throw new BusinessException(ReturnCode.PARAMETER_VALUE_ERROR);
        }
        ShippingVo shippingVo = new ShippingVo();
        BeanUtils.copyProperties(shipping, shippingVo);
        shippingVo.setCreateTime(DateTimeHelper.dateToString(shipping.getCreateTime()));
        shippingVo.setUpdateTime(DateTimeHelper.dateToString(shipping.getUpdateTime()));
        return shippingVo;
    }
}
