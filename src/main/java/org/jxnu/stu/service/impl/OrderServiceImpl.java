package org.jxnu.stu.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayResponse;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.jxnu.stu.common.BusinessException;
import org.jxnu.stu.common.Constant;
import org.jxnu.stu.common.ReturnCode;
import org.jxnu.stu.common.ServerResponse;
import org.jxnu.stu.controller.vo.ShippingVo;
import org.jxnu.stu.dao.CartMapper;
import org.jxnu.stu.dao.OrderItemMapper;
import org.jxnu.stu.dao.OrderMapper;
import org.jxnu.stu.dao.ProductMapper;
import org.jxnu.stu.dao.pojo.Cart;
import org.jxnu.stu.dao.pojo.Order;
import org.jxnu.stu.dao.pojo.OrderItem;
import org.jxnu.stu.dao.pojo.Product;
import org.jxnu.stu.service.OrderService;
import org.jxnu.stu.service.ShippingService;
import org.jxnu.stu.util.BigDecimalHelper;
import org.jxnu.stu.util.FTPHelper;
import org.jxnu.stu.util.PropertiesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    private Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ShippingService shippingService;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    private static AlipayTradeService tradeService;

    public Map pay(String orderNo,Integer userId,String path) throws Exception {
        Order order = orderMapper.selectByOrderNoAndUserId(orderNo, userId);
        if(order == null){
            throw new BusinessException(ReturnCode.ORDER_NOT_EXIST);
        }
        List<OrderItem> orderItems = orderItemMapper.selectByOrderNoAndUserId(orderNo, userId);
        if(orderItems.size() < 1){
            throw new BusinessException(ReturnCode.ERROR,"订单详情不存在");
        }
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = orderNo;

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = "mmall当面付扫码消费";

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
        int quantity = 0;
        for(OrderItem orderItem:orderItems){
            quantity += orderItem.getQuantity();
            goodsDetailList.add(GoodsDetail.newInstance(orderItem.getProductId().toString(),orderItem.getProductName(),
                    BigDecimalHelper.mul(orderItem.getCurrentUnitPrice(),new BigDecimal("100")).longValue(),
                    orderItem.getQuantity()));
        }

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = "购买"+quantity+"件商品共"+totalAmount+"元";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesHelper.getProperties("zfbinfo.callback.http.prefix"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        Configs.init(PropertiesHelper.getProperties("zfbinfo.properties.location"));
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                File dirFile = new File(path);
                if(!dirFile.exists()){
                    dirFile.setWritable(true);
                    dirFile.mkdirs();
                }
                // 需要修改为运行机器上的路径
                String filePath = String.format(path + "/qr-%s.png",
                        response.getOutTradeNo());
                logger.info("filePath:" + filePath);
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
                File targetFile = new File(filePath);
                try {
                    FTPHelper.uploadFile(Lists.newArrayList(targetFile));
                } catch (Exception e) {
                    logger.error("上传二维码失败",e);
                    throw new Exception(e);
                }
                Map<String,String> map = new HashMap<>();
                map.put("orderNo",outTradeNo);
                map.put("qrPath",PropertiesHelper.getProperties("ftp.server.http.prefix")+targetFile.getName());
                return map;
            case FAILED:
                logger.error("支付宝预下单失败!!!");
                throw new BusinessException(ReturnCode.ERROR,"支付宝预下单失败!!!");

            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                throw new BusinessException(ReturnCode.ERROR,"系统异常，预下单状态未知!!!");

            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                throw new BusinessException(ReturnCode.ERROR,"不支持的交易状态，交易返回异常!!!");
        }

    }

    @Override
    public Boolean queryOrderPayStatus(String orderNo, Integer userId) throws BusinessException {
        Order order = orderMapper.selectByOrderNoAndUserId(orderNo, userId);
        if(order == null){
            throw new BusinessException(ReturnCode.ORDER_NOT_EXIST);
        }
        if(order.getStatus() > Constant.OrderStatus.ORDER_NOT_PAY.getStatusCode()){
            return true;
        }
        return false;
    }

    @Override
    public String alipayCallback(HttpServletRequest request) throws BusinessException {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String,String> map = Maps.newHashMap();
        Iterator<String> iterator = parameterMap.keySet().iterator();
        while (iterator.hasNext()){
            String key = iterator.next();
            String[] values = parameterMap.get(key);
            StringBuffer stringBuffer = new StringBuffer();
            for(int i=0;i<values.length;i++){
                if(i==0){
                    stringBuffer.append(values[i]);
                }else {
                    stringBuffer.append(","+values[i]);
                }
            }
            map.put(key,new String(stringBuffer));
        }
        //开始验签
        try {
            boolean rsaCheckV2 = AlipaySignature.rsaCheckV2(map, Configs.getPublicKey(), "UTF-8", Configs.getSignType());
            if(!rsaCheckV2){
                throw new BusinessException(ReturnCode.ERROR,"恶意请求将提交网警处理！");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝验签失败",e);
            throw new BusinessException(ReturnCode.ERROR,"支付宝验签失败");
        }
        //判断是否为重复通知
        String out_trade_no = map.get("out_trade_no");
        Order callBackOrder = orderMapper.selectByOrderNo(out_trade_no);
        if(callBackOrder == null){
            logger.info(ReturnCode.ALIPAY_CALLBACK_ORDER_NOT_EXIST.getMsg());
            return "false";
        }
        if(callBackOrder.getStatus() >= Constant.OrderStatus.ORDER_PAYED.getStatusCode()){
            logger.info(ReturnCode.ALIPAY_CALLBACK_REPETOR.getMsg());
            return "false";
        }
        //判断total_amount是否确实为该订单的实际金额
        if(new BigDecimal(map.get("total_amount")).compareTo(callBackOrder.getPayment()) != 0){
            logger.info(ReturnCode.ALIPAY_CALLBACK_AMOUNT_NOT_EQUAL.getMsg());
            return "false";
        }
        //这里我们就不判断seller_id了，因为没有签约
        return "success";
    }

    /**
     * 创建订单
     * @param shippingId
     * @param userId
     * @return
     * @throws BusinessException
     */
    @Override
    public ServerResponse create(Integer shippingId, Integer userId) throws BusinessException {
        ShippingVo shippingVo = shippingService.select(shippingId, userId);
        List<Cart> cartList = cartMapper.selectCheckedByUserId(userId);
        List<OrderItem> orderItemList = this.productOrderItem(userId, cartList);
        //封装Order
        Order order = new Order();
        order.setOrderNo(productOrderNo());
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setPayment(this.getPayment(orderItemList));

        order.setPostage(0);

        return null;
    }

    private BigDecimal getPayment(List<OrderItem> orderItemList){
        BigDecimal payment = new BigDecimal("0");
        for(OrderItem orderItem:orderItemList){
            payment = BigDecimalHelper.add(payment, orderItem.getTotalPrice());
        }
        return payment;
    }

    private long productOrderNo(){
        return System.currentTimeMillis()+System.currentTimeMillis()%9;
    }

    private List<OrderItem> productOrderItem(Integer userId,List<Cart> cartList){
        List<OrderItem> orderItemList = new ArrayList<>();
        for(Cart cartItem : cartList){
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            orderItem.setUserId(userId);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setTotalPrice(BigDecimalHelper.mul(product.getPrice(),new BigDecimal(cartItem.getQuantity())));
            orderItemList.add(orderItem);
        }
        return orderItemList;
    }

    /**
     * 阿里支付简单打印应答
     * @param response
     */
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }
}
