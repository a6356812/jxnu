package org.jxnu.stu.common;

public interface CommonReturn {

    public int getCode();
    public String getMsg();
    public ReturnCode setMsg(String errMsg);
}
