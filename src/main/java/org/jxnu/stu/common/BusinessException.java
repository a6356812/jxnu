package org.jxnu.stu.common;

public class BusinessException extends Exception implements CommonReturn{

    private ReturnCode returnCode;

    public BusinessException(ReturnCode returnCode){
        super();
        this.returnCode = returnCode;
    }
    public BusinessException(ReturnCode returnCode,String errMsg){
        super();
        this.returnCode = returnCode;
        this.returnCode = this.returnCode.setMsg(errMsg);
    }
    @Override
    public int getCode(){
        return  returnCode.getCode();
    }
    @Override
    public String getMsg(){
        return  returnCode.getMsg();
    }
    @Override
    public ReturnCode setMsg(String errMsg) {
        return returnCode.setMsg(errMsg);
    }
}
