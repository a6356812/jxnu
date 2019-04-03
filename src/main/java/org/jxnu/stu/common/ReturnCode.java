package org.jxnu.stu.common;

public enum ReturnCode implements CommonReturn{
    //start from 10000 to 20000 be used to description Common Error
    ERROR(10000,"发生未知错误"),
    SUCCESS(10001,"成功！"),
    PARAMETER_VALUE_ERROR(10005,"参数异常"),
    //start from 20000 to 30000 be used to description User Error
    USER_NOT_EXIST(20001,"用户不存在"),
    USER_ALREADY_EXIST(20002,"用户已经存在"),
    USER_LOGIN_FAILED(20003,"用户名或密码错误"),
    USER_LOGIN_SUCCESS(20004,"登陆成功！"),
    USER_REGISTE_FAILD(20005,"用户注册失败"),
    USER_NOT_LOGIN(20006,"用户没有登陆"),
    USER_NOT_HAS_QUESTION(20007,"用户不存在或用户没有设置密保问题"),
    USER_ANSWER_WRONG(20008,"密保问题或答案错误"),
    USER_FORGETTOKEN_ERROR(20009,"重置密码forgetToken不一致"),
    USER_RESET_PASSWORD_ERROR(20010,"用户重置密码失败"),
    USER_INFO_UPDATE_ERROR(20011,"用户信息更新失败"),

    //the encode string is null

    ;
    private int errCode;
    private String errMsg;
    private ReturnCode(int errCode,String errMsg){
        this.errCode = errCode;
        this.errMsg = errMsg;
    }
    @Override
    public int getCode() {
        return errCode;
    }
    @Override
    public String getMsg() {
        return errMsg;
    }
    @Override
    public ReturnCode setMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }

}
