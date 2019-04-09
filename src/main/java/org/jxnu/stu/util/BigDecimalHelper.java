package org.jxnu.stu.util;

import java.math.BigDecimal;

public class BigDecimalHelper {

    private BigDecimalHelper(){
    }

    public static BigDecimal add(BigDecimal v1,BigDecimal v2){
        BigDecimal bigDecimal = new BigDecimal(v1.toString());
        BigDecimal bigDecima2 = new BigDecimal(v2.toString());
        return bigDecimal.add(bigDecima2);
    }

    public static BigDecimal sub(BigDecimal v1,BigDecimal v2){
        BigDecimal bigDecimal = new BigDecimal(v1.toString());
        BigDecimal bigDecima2 = new BigDecimal(v2.toString());
        return bigDecimal.subtract(bigDecima2);
    }

    public static BigDecimal mul(BigDecimal v1,BigDecimal v2){
        BigDecimal bigDecimal = new BigDecimal(v1.toString());
        BigDecimal bigDecima2 = new BigDecimal(v2.toString());
        return bigDecimal.multiply(bigDecima2);
    }

    public static BigDecimal div(BigDecimal v1,BigDecimal v2){
        BigDecimal bigDecimal = new BigDecimal(v1.toString());
        BigDecimal bigDecima2 = new BigDecimal(v2.toString());
        return bigDecimal.divide(bigDecima2);
    }

}
