package org.jxnu.stu.controller.backend;

import com.github.pagehelper.PageInfo;
import org.jxnu.stu.common.BusinessException;
import org.jxnu.stu.common.Constant;
import org.jxnu.stu.common.ReturnCode;
import org.jxnu.stu.common.ServerResponse;
import org.jxnu.stu.controller.vo.OrderVo;
import org.jxnu.stu.controller.vo.UserVo;
import org.jxnu.stu.dao.OrderMapper;
import org.jxnu.stu.dao.pojo.Order;
import org.jxnu.stu.service.OrderService;
import org.jxnu.stu.util.CookieHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/manage/order")
@Controller
public class OrderManageController {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 列出所有的订单详情
     * @param pageSize
     * @param pageNum
     * @return
     * @throws BusinessException
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(defaultValue = "10") Integer pageSize,
                                         @RequestParam(defaultValue = "1") Integer pageNum) throws BusinessException {
        PageInfo<OrderVo> orderVoPageInfo = orderService.listAll(pageSize, pageNum);
        return ServerResponse.createServerResponse(ReturnCode.SUCCESS.getCode(),orderVoPageInfo);
    }

    @RequestMapping(value = "/search",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<OrderVo> search(Long orderNo) throws BusinessException {
        OrderVo orderVo = orderService.detail(orderNo);
        return ServerResponse.createServerResponse(ReturnCode.SUCCESS.getCode(),orderVo);
    }

    @RequestMapping(value = "/send_goods",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> sendGoods(Long orderNo) throws BusinessException{
        int res = orderMapper.updateStatusByOrderNo(orderNo, Constant.OrderStatus.ORDER_SHIPPED.getStatusCode());
        return res > 0 ? ServerResponse.createServerResponse(ReturnCode.SUCCESS.getCode(),null,"发货成功")
                : ServerResponse.createServerResponse(ReturnCode.ORDER_DELIVER_FAILD.getCode(),null,ReturnCode.ORDER_DELIVER_FAILD.getMsg());
    }

}
