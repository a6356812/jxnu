package org.jxnu.stu.common;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable {

    //default value ,means response is failed
    private int status = 0;
    private String msg;
    private T data;

    private ServerResponse() {
    }

    private ServerResponse(int status) {
        this.status = status;
    }

    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResponse(String msg, T data) {
        this.msg = msg;
        this.data = data;
    }

    private ServerResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ServerResponse<T> createServerResponse(int status) {
        return new ServerResponse<T>(status);
    }

    public static <T> ServerResponse<T> createServerResponse(int status, T data) {
        return new ServerResponse<T>(status,null, data);
    }

    public static <T> ServerResponse<T> createServerResponse(int status, String msg) {
        return new ServerResponse(status, msg,null);
    }

    public static <T> ServerResponse<T> createServerResponse(int status, String msg, T data) {
        return new ServerResponse<T>(status, msg, data);
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
}
