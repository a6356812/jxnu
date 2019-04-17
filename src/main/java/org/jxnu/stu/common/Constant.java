package org.jxnu.stu.common;


import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Constant class
 */
public class Constant {

    public static final String CURRENT_USER = "currentUser";
    public static final String USER_FORGET_TOKEN = "forgetToken";

    public static final int USER_ADMIN = 0;
    public static final int USER_ORDINARY = 1;

    public static final String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    public static final String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";

    public static final int CHECKED = 1;
    public static final int UNCHECKED = 0;

    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_asc","price_desc");
    }

    public interface AliPayCallBack{
        String CALLBACK_SUCCESS = "success";
        String CALLBACK_FAILED = "failed";
    }

    public enum  OrderStatus{
        ORDER_CANCLE(0,"已取消"),
        ORDER_NOT_PAY(10,"代付款"),
        ORDER_PAYED(20,"已付款"),
        ORDER_SHIPPED(40,"已发货"),
        ORDER_SUCCESS(50,"订单已完成"),
        ORDER_CLOSE(60,"订单已关闭"),
        ;
        private int statusCode;
        private String statusMsg;
        OrderStatus(Integer statusCode,String statusMsg){
            this.statusCode = statusCode;
            this.statusMsg = statusMsg;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getStatusMsg() {
            return statusMsg;
        }

        public static OrderStatus codeOf(Integer statusCode) throws BusinessException {
            for(OrderStatus orderStatus:values()){
                if(statusCode.intValue() == orderStatus.getStatusCode()){
                    return orderStatus;
                }
            }
            throw new BusinessException(ReturnCode.ORDER_STATUS_NOT_EXIST);
        }
    }

    public static final boolean CATEGORY_NORMAL = true;
    public static final boolean CATEGORY_ABANDON = false;

}
