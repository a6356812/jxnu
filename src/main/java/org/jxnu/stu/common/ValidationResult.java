package org.jxnu.stu.common;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class ValidationResult {

    private boolean hasError = false;
    private Map<String,String> errMsgMap = new HashMap<>();

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public Map<String, String> getErrMsgMap() {
        return errMsgMap;
    }

    public void setErrMsgMap(Map<String, String> errMsgMap) {
        this.errMsgMap = errMsgMap;
    }

    public String getErrMsg(){
       return StringUtils.join(errMsgMap.values().toArray(),",");
    }
}
