package com.neure.agent.model;

import com.neure.agent.utils.JacksonUtils;

/**
 * Response
 *
 * @author tc
 * @date 2024-02-26 00:48
 */
public class DefaultResponse<T> {

    private int code;
    private String message;
    private T body;
    private int size;

    public static String buildError() {
        DefaultResponse r = new DefaultResponse();
        r.code = 500;
        r.message = "http请求出错";
        return JacksonUtils.ObjectToJsonStr(r);
    }

    public static DefaultResponse Error() {
        DefaultResponse r = new DefaultResponse();
        r.code = 500;
        r.message = "http请求出错";
        return r;
    }

    public  boolean isSuccess() {
        return this.code == 200;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
