package org.jxnu.stu.util;

import java.math.BigDecimal;

public class BigDecimalHelper {

    private BigDecimalHelper(){
    }

    public static BigDecimal add(Double v1,Double v2){
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(v1));
        BigDecimal bigDecima2 = new BigDecimal(String.valueOf(v2));
        return bigDecimal.add(bigDecima2);
    }

    public static BigDecimal sub(Double v1,Double v2){
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(v1));
        BigDecimal bigDecima2 = new BigDecimal(String.valueOf(v2));
        return bigDecimal.subtract(bigDecima2);
    }

    public static BigDecimal mul(Double v1,Double v2){
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(v1));
        BigDecimal bigDecima2 = new BigDecimal(String.valueOf(v2));
        return bigDecimal.multiply(bigDecima2);
    }

    public static BigDecimal div(Double v1,Double v2){
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(v1));
        BigDecimal bigDecima2 = new BigDecimal(String.valueOf(v2));
        return bigDecimal.divide(bigDecima2);
    }

}
